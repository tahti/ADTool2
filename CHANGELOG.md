# Change Log #
All notable changes to the ADTool will be documented in this file.
## Unreleased
  - added "Close all trees" menu item
  - fxed positioning of baloon window showing comments
  - added pictures to "Load Example Tree" dialog
## [2.2.0] - 2016-08-16 ##
  - added custom domains
  - added import from terms/txt file
  - fixed missing undo/redo for copy/pasting nodes
  - fixed issue with wrong size of nodes when adding/removing domain
  - fixed issue where folding tree in the domain messed up redo/undo
  - fixed issue with scrolling of the tree (issue #3)
  - fixed issue with undo after moving node left/right (issue #10)
  - fixed issue when importing and no tree is open (issue #7)
  - fixed issue with comment not being imported from XML
  - improved error messages for command line options
  - added option change number of ranked attacks
  - added option to export or rank only chosen domains via command line

## [2.1.1] - 2016-04-28 ##
  - fixed not unfolding with space
  - fixed adding countermeasure not working
  - fixed not showing up dialog to input k for domains with parameter
  - fixed issue with opening some XML files form older versions of ADTool and
    improved import of XML files from TreeMaker

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

## [2.0.0] - 2016-02-25 ##

## [1.4] - 2015-01-21 ##
  - added possibility of launching the program from the command line.
  - updated example *Breaking into a Warehouse* - fixing typo.

## [1.3] - 2013-12-06 ##
  -  Improving the time complexity of outputting an ADTree 
     after having modified its ADTerm. The algorithm to calculate the tree edit distance 
     has been changed from a cubic one 
     to a suboptimal one but with quadratic complexity. The new algorithm is based on 
     Euler string for trees and Levenshtein distance.
  -  Enhancing import and export of XML files with a possibility to also 
     include attribute values.
  -  Added a possibility of using special characters in node labels. Comma and parentheses are still not allowed.
  -  Added possibility to (alphabetically) sort columns in the valuations view window.
  -  Added the feature of highlighting on the ADTree representation 
     the node currently selected in the valuations view window.
  -  Implemented the keyboard shortcut [CTRL+Enter] to close the input dialog for editing 
     node labels.


## [1.2] - 2013-10-01 ##
  - added a possibility to create multi-line node labels
  - added the feature of exporting and importing trees written as XML files
  - added the *Remove Domain* button to the attribute window
  - improved management of the *Valuations View* and *Domain Details View* windows
  - several bugs removed

## [1.1] - 2013-06-05 ##
  - added attribute domain for *Difficulty for the proponent (L,M,H)*
  - added attribute domain for *Difficulty for the proponent (L,M,H,E)*
  - updated attribute domain *Minimal skill level needed for the proponent* 
    - default value for the proponent changed from *Infinity* to *k*
    - improved description of the attribute on the right hand side of 
      the *Add Attribute Domain* window
  - updated attribute domain for *Probability of success* 
    - rounding of inserted values removed
  - added *Help* menu item to the menubar
  - modified default name of the file for the *File, Save* option

## [1.0] - 2013-05-16 ##
Initial release of ADTool.
  - release of an updated version of the BankAccount.adt example.
