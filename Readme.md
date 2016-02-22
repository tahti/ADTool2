## Overview

The Attack Defense Tree Tool (ADTool) allows users to model and display attack defense
scenarios, through the use of attack defense trees (ADTrees) or an alternative term-based
representation of ADTrees called attack defense terms (ADTerms). It supports the methodology
developed within the ATREES project Since attack trees, protection trees, and defense trees
are formally instances of attack defense trees, the ADTool can also be employed
to automate and facilitate the usage of all aforementioned formalisms. Furthermore, the ADTool
allows to perform quantitative analyses on ADTrees/ADTerms. This means that a user is able to
answer questions such as: What are the costs of an attack, what is the minimal skill level required
for the attacker, how long does it take to implement all necessary defenses or who is the winner of
the considered attack defense scenario, and many others. Recently the tool have
been extended with posibility to model using Sequential Attack Trees.

In short:
- The ADTool allows the user to model attack defense scenarios using ADTrees and ADTerms.
- The ADTool allows the user to perform quantitative analyses on ADTrees/ADTerms.

## Compilation

ADTool is written in java. In order to compile the source code a
[Maven](https://maven.apache.org/) tool is necessary. After you have downloaded
and installed maven tool:
- clone the github repository: (`git clone git://github.com/tahti/ADTool2.git`)
- goto `project` directory (`cd project`)
- compile using maven (`mvn package`)
- after successful compilation there should be new directory `target` with ADTool-2.0-full.jar among other files
- run the tool using command `java -jar target/ADTool-2.0-full.jar`

