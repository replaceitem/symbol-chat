* Switched from ClothConfig to my own bundled config library
* Renamed `custom_symbols` config property to `favorite_symbols`. Existing config files will be converted and no custom symbols should be lost
* The hud can now be placed in all four corners instead of just top left and top right ([#61](https://github.com/replaceitem/symbol-chat/issues/61))
* The symbol suggestions now no longer move with the cursor when typing ([#62](https://github.com/replaceitem/symbol-chat/issues/62))
* Fixed chat bar having a gap for the symbol button when it's placed at the top ([#58](https://github.com/replaceitem/symbol-chat/issues/58))
* Missing glyphs are now no longer rendered in the Unicode table to prevent logs spam. Instead, a red bar is rendered. ([#59](https://github.com/replaceitem/symbol-chat/issues/59))
* The Unicode table options "show blocks", "text shadow" and "hide missing glyphs" are now persistent and can also be changed from the config ([#60](https://github.com/replaceitem/symbol-chat/issues/60))
