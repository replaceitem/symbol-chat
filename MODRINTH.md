# Symbol Chat

[<img alt="Available for fabric" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2.8.0/assets/cozy/supported/fabric_vector.svg">](https://fabricmc.net/)
[<img alt="Requires fabric api" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2.8.0/assets/cozy/requires/fabric-api_vector.svg">](https://modrinth.com/mod/fabric-api)
[<img alt="Works best with cloth config" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/requires/cloth-config-api_vector.svg">](https://modrinth.com/mod/cloth-config)
[<img alt="See me on GitHub" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2.8.0/assets/cozy/social/github-singular_vector.svg">](https://github.com/replaceitem)
[<img alt="Chat on Discord" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2.8.0/assets/cozy/social/discord-singular_vector.svg">](https://discord.gg/etTDQAVSgt)

<img src="https://raw.githubusercontent.com/replaceitem/symbol-chat/master/images/icon.png" align="right" width="128px"/>

This client side mod allows you to paste special characters into the chat, on signs, books, and in the anvil text box.
It also has support for a customizable symbol and kaomoji list.
Additionally, you can select a unicode font that is supported by minecraft,
and your typed text will automatically get converted into the supported characters.
All symbols are unifont symbols supported by minecraft, meaning all other players can see them without needing the mod on their client or the server.

## Basic usage

The symbol panel can be accessed by clicking the small button on the bottom left, available in several screens:

* Chat screen
* Anvil screen
* Sign edit screen
* Book editing screen

![Symbol menu](https://raw.githubusercontent.com/replaceitem/symbol-chat/master/images/symbol_menu.png)

When opened, on the top there are multiple tabs in which all symbols are grouped.
Clicking on a symbol in a tab will paste it into your chat box.
The first tab shows all available symbols of the following tabs and additionally has a search bar for finding a specific symbol.

![All symbols](https://raw.githubusercontent.com/replaceitem/symbol-chat/master/images/symbol_list.gif)

The next tabs group the symbols into various categories:

* Faces & People
* Nature & Food
* Things
* Activities, Transport & Places
* Symbols
* Shapes

The tab labeled `K` contains various kaomojis like `¯\_(ツ)_/¯`.

The last tab is a favorites tab, which you can add favorite symbols too as well as adding custom symbols.
To favorite a symbol, just right click it in the symbol panel.
Symbols can be un-favorited by just right-clicking it again.

## Symbol suggestions

For inserting symbols faster, you can use the symbol suggestion popup, which works similar to other chat platforms.
By typing a colon (`:`), a popup will open and shows symbols that match with what you type after.
For example, if you type `:cro` it will suggest a crown, crocodile, croissant, cross and crossed flags.

![Symbol menu](https://raw.githubusercontent.com/replaceitem/symbol-chat/master/images/suggestion.png)

If you just enter a colon without entering anything after, it will show the first couple favorited symbols for quick access.


## Fonts

On the top right of supported screens, you can choose between many different fonts.
You can select a font on the dropdown on the top right.
This will change the text you type to that font.
Fonts make use of unicode characters to produce a more interesting looking text.
These are also supported on clients without symbol-chat and can be seen by everyone.

![Fonts](https://raw.githubusercontent.com/replaceitem/symbol-chat/master/images/fonts.png)

## Config

Some aspects of this mod are configurable using [Cloth config](https://modrinth.com/mod/cloth-config) (not required).
The configuration screen is accessible through the gear button on the top of the chat screen, as well as the [ModMenu](https://modrinth.com/mod/modmenu) config button.

The config allows you to change the appearance and behavior of the symbol-chat HUD.
You can also add custom symbols not present in the tabs by adding them to the "Favorite symbols" text box.
There is also support for adding custom kaomojis.

![Config](https://raw.githubusercontent.com/replaceitem/symbol-chat/master/images/config.png)

## Unicode table

As an advanced feature, you can access a unicode table through the `⣿⣿` button next to the config buton.
This allows you to browse all unicode pages with the minecraft font to find new symbols.
You can also search symbols and directly add them to your favorites or copy them to the clipboard.

![Unicode table](https://raw.githubusercontent.com/replaceitem/symbol-chat/master/images/unicode.png)