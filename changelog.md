* Whether fonts are converted and symbols are suggested is now determined from the command syntax tree
  * When writing normally in chat, fonts will be used and symbols will be suggested
  * When writing in a command, only "message" and "greedy string" arguments convert fonts (such as in `/say` or `/msg`)
* Removed config options for font conversion and chat symbol suggestion regex