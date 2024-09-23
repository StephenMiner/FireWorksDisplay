/display create [name] (create a firework display)

/display launchfrom [name] (set the location where fireworks will be launched from

/display setlaunchpower [name] [whole number] (sets the launch power for fireworks launched, I forgot what this actually does.

/display setlaunchamount [name] [whole number] (sets how many fireworks will be launched when this display activated

/display setlaunchdelay [name] [whole number] (sets how many ticks there will be in between each firework shot by the display)

/display addEffect [name] [fireworkeffect] (see the tab completer to see all the available effects, this is like the firework star)

/display removeEffect [name] [effect] (removes the specified effect

/display addColor [name] [r,g,b] (adds the specified color formated as "r,g,b" where r, g, and b are red green blue whole number values)

/display removeColor [name] [r,g,b] (removes the specified color)

/display addFadeColor [name] [r,g,b] (adds fade color, dont actually remember what this does for the firework)
 
/display withTrail [name] [true/false] (Defines whether launched fireworks will be launched with a trail or not)

/display withFlicker [name] [true/false] (Defines whether launched fireworks will have the flicker effect or not) 

/launcher create [name] (creates a firework launcher)

/launcher addDisplays [name] [firework display name] (Adds the specified firework displays to the specified launcher)

/launcher removeDisplay [name] [firework display name] (Removes the specified firework display from the specified launcher)

/launcher setInterval [name] [whole number] (If you want the launcher to be infinitely repeating until turned off, this sets the interval in ticks at which it will activate all of the firework displays that are a part of this launcher)

/launcher setTrigger [name] (Lets you right click a block to set it as a trigger block so the launcher will be activated when that block is clicked)

/launcher giveRemote [name] (Gives a remote to activate/deactivate the specified firework display)

/launcher reload [name] (Reloads the specified firework launcher, forcing any changes upon this launcher if it is already activated)





