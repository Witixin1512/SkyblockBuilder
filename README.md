# Skyblock Builder
A minecraft mod which lets you generate custom skyblock islands by using config.

[![CurseForge](http://cf.way2muchnoise.eu/full_446691_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/skyblock-builder)
[![Curseforge](http://cf.way2muchnoise.eu/versions/For%20MC_446691_all.svg)](https://www.curseforge.com/minecraft/mc-mods/skyblock-builder)

## How to use
### Setting world type on singleplayer
You can simply set `World Type` to `Skyblock` when creating a new world on a click on `More World Options...`

### Setting world type on server
You can simply set `level-type` in `server.properties` to `custom_skyblock`.

### Creating a custom skyblock island
1. Build an island.
2. Use the vanilla Structure Block if your island is smaller or equals 48x48x48 blocks to save it as a `.nbt` file. 
Otherwise, you need to use a mod like [Create](https://www.curseforge.com/minecraft/mc-mods/create) to generate this file.
3. Copy the generated file from `saves/<world>/generated/minecraft/structures/<name>.nbt` to `config/skyblockbuilder/template.nbt`.
4. Set the possible spawns in `config/skyblockbuilder/spawns.json`. There can be multiple spawns, each one is an array with `[x, y, z]`
relative to the 0, 0, 0 from the template structure. 
5. To view your current spawns, you need to use the `/reload` command to reload the config. After that, you need to use the 
`/skyblock spawns true` command to view all possible spawn points.
6. Repeat step 4 and 5 until everything is correct.

### Possible spawns
Possible spawns are set in `config/skyblockbuilder/spawns.json`. For each player, the game will choose a random position
and places the player on that position. Good on big islands when adding a lot of players at once at one team.

### Loot chests on island
If you want a loot chest on an island, you need to set the NBT data to the chest with the `/data merge block x y z {LootTable: modid:path/to/loot_table}`
command to set it as loot chest. WARNING! Do not open that chest after merging this data into the chest.

### Starting inventory
You can set a starting inventory by customising `config/skyblockbuilder/starter_items.json`. These items will be given 
to the player **only** on **initial joining world**, not when joining a team. You can also set the items to a special 
slot with key `Slot`. Available values for the slots are:
- `mainhand` (default)
- `offhand`
- `head`
- `chest`
- `legs`
- `feet`

The config could look like this:
```json
{
  "items": [
    {
      "item": "minecraft:diamond_pickaxe",
      "nbt": {
        "Unbreakable": true
      }
    },
    {
      "item": "minecraft:bread",
      "count": 32,
      "Slot": "offhand"
    }
  ]
}
```
If you want that every other item will be deleted, you can simply set the config option `inventory.clear` to true. This 
will delete items like guide books or other things. That way, you don't have to go through all configs to enable these 
items and could just add them to the starter items.

### Allow structures to be generated
Structures can be generated by changing the config at `config/skyblockbuilder/config.toml`. Nether structures are generated 
by default, but you can turn that off. Overworld structures are not being generated by default.


## Normal user
### Listing teams
Everyone can list the teams. For that, you can use `/skyblock list <name>`. That will list either all the teams if no 
name is provided or the names of all players in a team.

### Teleporting back to home island
If home command is enabled in the config, you can teleport back to your teams island with `/skyblock home`.

### Teleporting to spawn island
If teleporting to spawn is enabled in the config, you can teleport to spawn island with `/skyblock spawn`.

### Visiting other islands
If visits are enabled in the config and a team enabled visiting, you can visit an island with `/skyblock visit <team>`.

### Enable visiting
You can see your teams current visiting state with `/skyblock team allowVisits` and enable/disable it with `/skyblock team 
allowVisits <true/false>`.

### Create an own island
If users can create islands with their own command, you can use `/skyblock create <name> <players>`. This will create a 
team, and the given players will be added to the new team. If no players are given, the user who executes the command will 
be added to the team. If no name is given, a random name will be generated.

### Rename team island
You can rename a/your team by using `/skyblock team <new name> <team name>`. `<team name>` is optional. Can be used by 
any team member. Users with permission level 2 could also edit other teams' name.

### Modify spawns
If enabled in the config, you can modify spawns from your team. You can add them with `/skyblock team spawns add <pos>`. If 
no position is given, the current position will be used. Remove them with `/skyblock team spawns remove <pos>`. Same as 
before: position is optional. For users with permission level 2: `/skyblock team spawns reset <team>` will reset the 
spawn points to the default ones. To add the spawns, you need to be within the range specified in the config.


## Invitations
### Inviting users
Everyone in a team can invite other players. For that, you can use `/skyblock invite <player>`. That will send an invitation 
to the given player. Only players with no team can be invited.

### Accepting invitations
Everyone with an invitation can accept them. You can only accept invitations if you're in no team. For that, you use 
the command `/skyblock accept <team>`.
You can also decline an invitation by using the command `/skyblock decline <team>`.


## Join requests
### Sending a join request
You can send a join request to all teams if you're currently in no team by using `/skyblock join <team>`. That will send a 
request to the team and each player in the team can accept or deny your request.

### Accepting requests
Each user of a team can use `/skyblock team accept <player>` to accept the given player. That will teleport the player to 
your island.
You can also deny the request by using `/skyblock team deny <player>`.


## Using teams
Only an operator with permission level 2 or higher can change anything in the `/skyblock manage` category.
### Creating teams
Use the `/skyblock manage teams create <name>` command to generate a team with the given name. If no name if provided, a random
name will be generated.

Alternatively (good for servers) you can use `/skyblock manage teams createAndJoin <name>` to create the team and join it.

### Deleting teams
Use the `/skyblock manage teams delete <name>` to delete the team with the given name. WARNING! This cannot be undone. The island
will still exist but you can't re-bind a new team to that island. If users are in the team, they will be teleported to spawn 
after dropping all their items.

### Clearing teams
Because teams can be empty, you can "clear" all islands. If you use `/skyblock manage teams clear <name>`, all empty teams will be deleted 
as in [Deleting teams](#deleting-teams).

If you provide a team name, all players from this team will be removed and teleported to spawn island.

### Joining a team
An operator need to add players to a team. For that, they need to use `/skyblock manage addPlayer <player> <team_name>`.
Then the player will be teleported to the teams' island.

### Leaving a team
An operator need to remove players from a team. For that, they need to use `/skyblock manage kickPlayer <player>`. The removed
player will be teleported back to spawn after dropping all the items in the inventory.

If you're not op and want to leave your team, you can simply type in `/skyblock leave`. You will drop all your items and 
be teleported to spawn.