# GroupSystem
### What is this project? 
This is my trial plugin for the PlayLegend-network. This is a group and permission system equal to LuckPerms. 

### Commands
- `/creategroup <key> <name> <color> <priority> <prefix>` - Create a new group. The group mustn't already exist.   
- `/deletegroup <key>` - Delete an existing group.
- `/getgroup (<player>)` - Get the group information about yourself or another player   
- `/setcolor <key> <color>` - Set the color of an existing group to the color. Example color code: *&8*   
- `/setdisplayname <key> <display_name>` - Set the display name of an existing group to the provided displayname. Maximum length: **50 characters**
- `/setpriority <priority>` - Set the tablist priority of an existing group. Maximum digits: **5** (if higher, it gets shorten to 5 -> `1320000` to `13200`)
- `/setprefix <prefix>` - Set the prefix of an existing group. Maximum length: **50 characters**
- `/setgroup <player> (<days> <hours> <minutes>)` - Set the group of a player to a specific time or permanent