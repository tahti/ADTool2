# Change Log #
All notable changes to the ADTool will be documented in this file.

## Unreleased
  - fixed missing undo/redo for copy/pasting nodes
  - fixed issue with wrong size of nodes when adding/removing domain

## [2.1.1] - 2016-04-28 ##
  - fixed not unfolding with space
  - fixed adding countermeasure not working
  - fixed not showing up dialog to input k for domains with parameter
  - fixed issue with opening some xml files form older versions of ADTool and
    improved import of xml files from TreeMaker

## [2.1.0] - 2016-04-16 ##
  - changed behaviour of drag to be independend of whether tree or background is dragged
  - added zoom in, zoom out to view menu
  - added comments field to nodes
  - fixed issue that it was possibile to add sibling to counter node
  - added possibility to import adt files
  - added redo undo operations

## [2.0.3] - 2016-03-31 ##
  - added command line examples
  - fixed regression bug when exporting ranking from command line
  - renaming node now perserves the node value in domains
  - added rounding of real numbers to three digits in display

## [2.0.2] - 2016-03-21 ##
  - added copy/paste of values in Domain View
  - fixed not updating node size after checking "Computed Values" checkbox
  - added copy/paste of values in Valuations View
  - added command line options
  - fixed several bugs in wrong/missing messages
  - fixed bug when marking ranking on countered node with no children
  - fixed bug where windows could be dragged outside a working area
  - added command line option to export ranking
  - changed labels to be visible in domains by default
  - code cleanup to remove warnings
  - fixed print preview dialog being over print dialog
  - fixed class cast exception when doing copy paste in SandDomain
  - fixed bug that made it impossible to edit value in domain after changing name of the node in term view-
  - added sorting values in valuations view by name of the node

## [2.0.1] - 2016-03-01 ##
  - fixed bug with wrong Sand Tree terms
  - changed copy/paste behaviour to take into account Attacker/defender role so
    that visual colour stays the same
  - fixed Valuations View for Attack Defense Trees
  - added marking when highlighting values in Valuations View
  - added disabling "Validate" button when terms text is empty
  - added option to show computed values in countered nodes with no children
  - added option not to load saved layout when loading failed last time

## [2.0.0] - 2016-02-25 ##
Initial release of the second rewrite of ADTool. Among others there are new features:
  - support opening multiple trees
  - support for Sand Attack Trees
  - fixed issue of ADTool not working under MacOS
  - ranking of attacks
  - copy/paste, rerdering of nodes
  - automatic layout saving on exit
