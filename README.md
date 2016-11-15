# Ray  [![Build Status](https://travis-ci.org/Wundero/Ray.svg?branch=master)](https://travis-ci.org/Wundero/Ray) [![codebeat badge](https://codebeat.co/badges/babc8e5b-0a8f-422f-8017-5ea8f0c88fac)](https://codebeat.co/projects/github-com-wundero-ray) [![Github All Releases](https://img.shields.io/github/downloads/Wundero/Ray/total.svg)](https://github.com/Wundero/Ray/releases/latest) [![GitHub issues](https://img.shields.io/github/issues/Wundero/Ray.svg)](https://github.com/Wundero/Ray/issues) [![GitHub pull requests](https://img.shields.io/github/issues-pr/Wundero/Ray.svg)](https://github.com/Wundero/Ray/pulls)
Ray is an open source chat management system built for Sponge.

Download: [__[Here]__](https://github.com/Wundero/Ray/releases/latest)

## Requirements
This plugin requires SpongeAPI version 5.X or newer.
# Features
This plugin allows you to completely control the formatting of chat messages.
## Groups
This plugin uses "groups" to section users off into separate, permission based format selections. Each group has an optional permission and priority; the groups with the highest number for priority will be chosen over lower numbers. Each user will automatically be assigned a group based on their highest priority permitted group.
## Formats
Each group contains "formats", which are what handle text replacement in messages. Each format is backed by json based text, which replaces variables and parses json.

To create formats, it is __highly__ recommended to use the in-game format builder rather than editing the config, as it will prevent errors and other problems.

__View information about formats [[Here]](https://github.com/Wundero/Ray/wiki/Formats)__

Formats are split in two ways: Types and Contexts.
### Format Types
Format types are how the format is displayed; for instance, an animation displaying consecutive frames. Several types store internal format(s), which can be any type too. You can have an event format which triggers an animated format with multi formats containing translatables.

__View information about format types [[Here]](https://github.com/Wundero/Ray/wiki/Format-Types).__
### Format contexts
Format contexts are when and where the format is displayed; for instance, when players send chat messages. The plugin by default comes with several contexts built in.

__View information about format contexts [[Here]](https://github.com/Wundero/Ray/wiki/Format-contexts).__
## Channels
This plugin also has internal support for customizable ChatChannels, which are customizable and have an in-game builder.

__View information about channels [[Here]](https://github.com/Wundero/Ray/wiki/Channels).__
## Tags
Tags are a generic "button" that can be used in chat to create dynamic effects. There are several types of tags included in the plugin.

__View information about tags [[Here]](https://github.com/Wundero/Ray/wiki/Tags).__

### Extras
Download: [__[Here]__](https://github.com/Wundero/Ray/releases)

Source: [__[Here]__](https://github.com/Wundero/Ray/)

Wiki: [__[Here]__](https://github.com/Wundero/Ray/wiki)
