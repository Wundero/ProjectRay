# ProjectRay
Open Source chat manager


TODO

- death messages THIS ONE SUCKS :( I've been avoiding this because I don't even know how to go about it. Idea dump just in case I think one will work:
  - translation files:
    - for example, in a file: death.fall.void: %s fell into the void!
    - the formatting for %s n stuff would come from format data
    - allows for language support (get from client support :D)
    - variables might suck for this one, unless I don't use %s and instead opt for {...}
    - probably the one I will have to use
  - translation through client:
    - this one doesn't allow nice custom messages or whatever, but colors and formats are supported
    - I think I can pass it args like player name and killer and item and stuff
  - translation through server:
    - same thing as before, but grabbing locale serverside and translating it there
    - less performant but could be more customizable (not sure how though)
  - one format for every death type
    - call death-cactus or whatever format for deaths
    - most customizable (except I think translation files)
    - bulky af
  - multi-template formats:
    - example: death-cactus-1: {...} death-cactus-2: {...} death-lava: {...}
    - events would look for formatting template of proper name
    - problem is that it doesn't allow for proper conformation to format#send(...) calls; would need a way to do that
    - also bulky but less so than format per death type
- fix ranged obfuscation (and make numbers bigger)
- more format types (See FormatType.java for details)
- nicknames
- fully fledged permission system (not sure how to work this into the current setup though)
- displayname changing (prefix/suffix/name/other tags or whatever)
- different chat levels (i.e. shout/talk/whisper)
- spy
- program that nicely allows config editing (config looks like garbage but I can't fix that)
- bungee support (prolly through something other than bungee's system, such as sockets, but meh)
- chat filter
- urls in chat
- hooks to other sponge plugins
- economy?
- tags and other customizable clickable things (like buttons)
- proper logging (to console and file)
- sql/nosql database? (probably sql) - for server synching
- DOCUMENTATION (and javadocs)
- clearing chat
- first join (new server) groups null - IMPORTANT TO FIX THIS
- translation files
  - I think the framework is there, but I need to run tests. For now I will probably implement a testing command that lets you call whatever key and it translates that for you (passing args too?)
  - Once framework is done and dusted (afaik), I will need to create a bunch of these files. That's gonna suck.
- fake messages
- message editor/remover/adder (only considering since it consumes more memory than i think i would like)
  - might make this as a mod addon instead
- mail (only considering since i would have to deal with offline variables and i don't want to)
  - perhaps for mail i could serialize and store the map of info for the sender and then when read parse for recipient too?
- api
- more config templates
- more default generated args

more that i can't think of right now
