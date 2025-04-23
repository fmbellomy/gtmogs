package com.gregtechceu.gtceu.api.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredientExtensions;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;
import com.gregtechceu.gtceu.utils.GTTransferUtils;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

@ExtensionMethod(SizedIngredientExtensions.class)
public class NotifiableFluidTank extends NotifiableRecipeHandlerTrait<SizedFluidIngredient>
                                 implements ICapabilityTrait, IFluidHandlerModifiable {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(NotifiableFluidTank.class,
            NotifiableRecipeHandlerTrait.MANAGED_FIELD_HOLDER);
    @Getter
    public final IO handlerIO;
    @Getter
    public final IO capabilityIO;
    @Persisted
    @Getter
    protected final CustomFluidTank[] storages;
    @Getter
    protected boolean allowSameFluids; // Can different tanks be filled with the same fluid. It should be determined
                                       // while creating tanks.
    private Boolean isEmpty;

    @Persisted
    @DescSynced
    @Getter
    protected final CustomFluidTank lockedFluid = new CustomFluidTank(FluidType.BUCKET_VOLUME);
    @Getter
    protected Predicate<FluidStack> filter = f -> true;

    public NotifiableFluidTank(MetaMachine machine, int slots, int capacity, IO io, IO capabilityIO) {
        super(machine);
        this.handlerIO = io;
        this.storages = new CustomFluidTank[slots];
        this.capabilityIO = capabilityIO;
        for (int i = 0; i < this.storages.length; i++) {
            this.storages[i] = new CustomFluidTank(capacity);
            this.storages[i].setOnContentsChanged(this::onContentsChanged);
        }
    }

    public NotifiableFluidTank(MetaMachine machine, List<CustomFluidTank> storages, IO io, IO capabilityIO) {
        super(machine);
        this.handlerIO = io;
        this.storages = storages.toArray(CustomFluidTank[]::new);
        this.capabilityIO = capabilityIO;
        for (CustomFluidTank storage : this.storages) {
            storage.setOnContentsChanged(this::onContentsChanged);
        }
        if (io == IO.IN) {
            this.allowSameFluids = true;
        }
    }

    public NotifiableFluidTank(MetaMachine machine, int slots, int capacity, IO io) {
        this(machine, slots, capacity, io, io);
    }

    public NotifiableFluidTank(MetaMachine machine, List<CustomFluidTank> storages, IO io) {
        this(machine, storages, io, io);
    }

    // TODO Kross: See if this can be decoupled from CustomFluidTank
    public void onContentsChanged() {
        isEmpty = null;
        notifyListeners();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public List<SizedFluidIngredient> handleRecipeInner(IO io, GTRecipe recipe, List<SizedFluidIngredient> left,
                                                        boolean simulate) {
        if (io != handlerIO) return left;
        if (io != IO.IN && io != IO.OUT) return left.isEmpty() ? null : left;

        FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
        // Store the FluidStack in each slot after an operation
        // Necessary for simulation since we don't actually modify the slot's contents
        // Doesn't hurt for execution, and definitely cheaper than copying the entire storage
        FluidStack[] visited = new FluidStack[storages.length];
        for (var it = left.iterator(); it.hasNext();) {
            var ingredient = it.next();
            if (ingredient.ingredient().hasNoFluids()) {
                it.remove();
                continue;
            }

            var fluids = ingredient.getFluids();
            if (fluids.length == 0 || fluids[0].isEmpty()) {
                it.remove();
                continue;
            }

            if (io == IO.OUT && !allowSameFluids) {
                CustomFluidTank existing = null;
                for (var storage : storages) {
                    if (!storage.getFluid().isEmpty() &&
                            FluidStack.isSameFluidSameComponents(storage.getFluid(), fluids[0])) {
                        existing = storage;
                        break;
                    }
                }
                if (existing != null) {
                    FluidStack output = fluids[0];
                    int filled = existing.fill(output, action);
                    ingredient.shrink(filled);
                    if (ingredient.amount() <= 0) {
                        it.remove();
                    }
                    // Continue to next ingredient regardless of if we filled this ingredient completely
                    continue;
                }
            }

            for (int tank = 0; tank < storages.length; ++tank) {
                FluidStack stored = getFluidInTank(tank);
                int amount = (visited[tank] == null ? stored.getAmount() : visited[tank].getAmount());
                if (io == IO.IN) {
                    if (amount == 0) continue;
                    if ((visited[tank] == null && ingredient.test(stored)) || ingredient.test(visited[tank])) {
                        var drained = storages[tank].drain(ingredient.amount(), action);
                        if (drained.getAmount() > 0) {
                            visited[tank] = drained.copy();
                            visited[tank].setAmount(amount - drained.getAmount());
                            ingredient.shrink(drained.getAmount());
                        }
                    }
                } else { // IO.OUT && No tank already has this output
                    FluidStack output = fluids[0].copy();
                    output.setAmount(ingredient.amount());
                    if (visited[tank] == null || FluidStack.isSameFluidSameComponents(visited[tank], output)) {
                        int filled = storages[tank].fill(output, action);
                        if (filled > 0) {
                            visited[tank] = output.copy();
                            visited[tank].setAmount(filled);
                            ingredient.shrink(filled);
                            if (!allowSameFluids) {
                                if (ingredient.amount() <= 0) it.remove();
                                break;
                            }
                        }
                    }
                }

                if (ingredient.amount() <= 0) {
                    it.remove();
                    break;
                }
            }
        }
        return left.isEmpty() ? null : left;
    }

    @Override
    public boolean test(SizedFluidIngredient ingredient) {
        return !this.isLocked() || ingredient.test(this.lockedFluid.getFluid());
    }

    @Override
    public int getPriority() {
        return !isLocked() || lockedFluid.getFluid().isEmpty() ? super.getPriority() : HIGH - getTanks();
    }

    public boolean isLocked() {
        return !lockedFluid.getFluid().isEmpty();
    }

    public void setLocked(boolean locked) {
        setLocked(locked, storages[0].getFluid());
    }

    public void setLocked(boolean locked, FluidStack fluidStack) {
        if (this.isLocked() == locked) return;
        if (locked && !fluidStack.isEmpty()) {
            this.lockedFluid.setFluid(fluidStack.copy());
            this.lockedFluid.getFluid().setAmount(1);
            setFilter(stack -> FluidStack.isSameFluidSameComponents(stack, this.lockedFluid.getFluid()));
        } else {
            this.lockedFluid.setFluid(FluidStack.EMPTY);
            setFilter(stack -> true);
        }
        onContentsChanged();
    }

    public NotifiableFluidTank setFilter(Predicate<FluidStack> filter) {
        this.filter = filter;
        for (CustomFluidTank storage : storages) {
            storage.setValidator(filter);
        }
        return this;
    }

    @Override
    public RecipeCapability<SizedFluidIngredient> getCapability() {
        return FluidRecipeCapability.CAP;
    }

    public int getTanks() {
        return storages.length;
    }

    @Override
    public int getSize() {
        return getTanks();
    }

    @Override
    public @NotNull List<Object> getContents() {
        List<FluidStack> ingredients = new ArrayList<>();
        for (int i = 0; i < getTanks(); ++i) {
            FluidStack stack = getFluidInTank(i);
            if (!stack.isEmpty()) {
                ingredients.add(stack);
            }
        }
        return new ArrayList<>(ingredients);
    }

    @Override
    public double getTotalContentAmount() {
        long amount = 0;
        for (int i = 0; i < getTanks(); ++i) {
            FluidStack stack = getFluidInTank(i);
            if (!stack.isEmpty()) {
                amount += stack.getAmount();
            }
        }
        return amount;
    }

    public boolean isEmpty() {
        if (isEmpty == null) {
            isEmpty = true;
            for (CustomFluidTank storage : storages) {
                if (!storage.getFluid().isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }

    public void exportToNearby(@NotNull Direction... facings) {
        if (isEmpty()) return;
        var level = getMachine().getLevel();
        var pos = getMachine().getPos();
        for (Direction facing : facings) {
            var filter = getMachine().getFluidCapFilter(facing, IO.OUT);
            GTTransferUtils.getAdjacentFluidHandler(level, pos, facing)
                    .ifPresent(adj -> GTTransferUtils.transferFluidsFiltered(this, adj, filter));
        }
    }

    public void importFromNearby(@NotNull Direction... facings) {
        var level = getMachine().getLevel();
        var pos = getMachine().getPos();
        for (Direction facing : facings) {
            var filter = getMachine().getFluidCapFilter(facing, IO.IN);
            GTTransferUtils.getAdjacentFluidHandler(level, pos, facing)
                    .ifPresent(adj -> GTTransferUtils.transferFluidsFiltered(adj, this, filter));
        }
    }

    //////////////////////////////////////
    // ******* Capability ********//
    //////////////////////////////////////
    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return storages[tank].getFluid();
    }

    public void setFluidInTank(int tank, @NotNull FluidStack fluidStack) {
        storages[tank].setFluid(fluidStack);
    }

    @Override
    public int getTankCapacity(int tank) {
        return storages[tank].getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return storages[tank].isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!canCapInput()) return 0;
        return fillInternal(resource, action);
    }

    public int fillInternal(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        var copied = resource.copy();
        CustomFluidTank existingStorage = null;
        if (!allowSameFluids) {
            for (var storage : storages) {
                if (!storage.getFluid().isEmpty() &&
                        FluidStack.isSameFluidSameComponents(storage.getFluid(), resource)) {
                    existingStorage = storage;
                    break;
                }
            }
        }
        if (existingStorage == null) {
            for (var storage : storages) {
                var filled = storage.fill(copied.copy(), action);
                if (filled > 0) {
                    copied.shrink(filled);
                    if (!allowSameFluids) {
                        break;
                    }
                }
                if (copied.isEmpty()) break;
            }
        } else {
            copied.shrink(existingStorage.fill(copied.copy(), action));
        }
        return resource.getAmount() - copied.getAmount();
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (canCapOutput()) {
            return drainInternal(resource, action);
        }
        return FluidStack.EMPTY;
    }

    public FluidStack drainInternal(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;

        var copied = resource.copy();
        for (var storage : storages) {
            var candidate = copied.copy();
            copied.shrink(storage.drain(candidate, action).getAmount());
            if (copied.isEmpty()) break;
        }
        copied.setAmount(resource.getAmount() - copied.getAmount());
        return copied;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (canCapOutput()) {
            return drainInternal(maxDrain, action);
        }
        return FluidStack.EMPTY;
    }

    public FluidStack drainInternal(int maxDrain, FluidAction action) {
        if (maxDrain == 0) return FluidStack.EMPTY;
        FluidStack totalDrained = null;
        for (var storage : storages) {
            if (totalDrained == null || totalDrained.isEmpty()) {
                totalDrained = storage.drain(maxDrain, action);
                if (totalDrained.isEmpty()) {
                    totalDrained = null;
                } else {
                    maxDrain -= totalDrained.getAmount();
                }
            } else {
                FluidStack copy = totalDrained.copy();
                copy.setAmount(maxDrain);
                FluidStack drain = storage.drain(copy, action);
                totalDrained.grow(drain.getAmount());
                maxDrain -= drain.getAmount();
            }
            if (maxDrain <= 0) break;
        }
        return totalDrained == null ? FluidStack.EMPTY : totalDrained;
    }

    @Override
    public void onMachineLoad() {
        super.onMachineLoad();
        if (this.isLocked()) {
            setFilter(stack -> FluidStack.isSameFluidSameComponents(stack, this.lockedFluid.getFluid()));
        }
    }
}
