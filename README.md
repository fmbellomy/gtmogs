# GTMOGS - GregTech Modern Ore Generation: Standalone

Do you want to use GregTech's ore generation but not the *rest* of GregTech's absolutely enormous breadth of content? The time for celebration is upon you, my dearest pack developer.

For context, I put this together by cloning GT:M and just deleting everything that I could get rid of without breaking ore generation. Some very mild tweaking had to be done to oregen to unhook it from GT's custom ore system, and yet more mild tweaking had to be done to the map integrations to get those to work with conventional minecraft blocks.

As such, you can safely refer to [GTCEu's official documentation](https://gregtechceu.github.io/GregTech-Modern/Modpacks/Ore-Generation/) on ore generation for how to actually use this. 
(Assuming they don't massively overhaul oregen in the future and I never catch up. But that would never happen :clueless:)

## Features:
### Absolutely zero veins by default.
That's right. If you add this mod to a pack it will proceed to generate zero ores, coupled with the default behavior of disabling vanilla ore generation. After all, without GregTech's ore system, there's nothing to make veins out of to begin with.

The expected use case is that this mod will be used almost exclusively by modpacks willing to implement their own ore veins that suit their specific needs.

_cough cough modern industrialization pack devs cough cough_

### KubeJS Integration
If you're familiar with GT:M's kubejs integration for custom ore veins, then you already know about this.

This is pretty much the entire point of the mod. If you want to know more about it, go read the [GTM docs](https://gregtechceu.github.io/GregTech-Modern/Modpacks/Ore-Generation/) for it.

### EMI integration
In theory there should also be integration for JEI and REI, but I haven't tested those personally yet because I don't plan to use them. If you try using this with one of those mods and it doesn't work then make an issue and then I'll actually look into it.

### Map integration
This includes FTBChunks, JourneyMap, and Xaero's Mini/World Map (I also haven't tested Xaero's yet, so if it ends up being broken then put an issue here and I'll actually look at it.)

#### And a huge thanks to the devs behind GTCEu for the tremendous amount of work put into the base mod.
(Except for that I really am not doing this for the money, I just wanted cool ore veins because I'm a sigma.)