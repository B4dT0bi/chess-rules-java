[![Build Status][travis-image]][travis-url] [![Coverage Status](https://coveralls.io/repos/ChessCorp/chess-rules-java/badge.svg?branch=master&service=github)](https://coveralls.io/github/ChessCorp/chess-rules-java?branch=master)


# Chess Rules implementation in Java

## Using the library in your Java Application

If your project is using Maven, you can add the library as a dependency:

```xml
<dependency>
    <groupId>org.alcibiade</groupId>
    <artifactId>chess</artifactId>
    <version>1.0.0</version>
</dependency>
```

Otherwise, download the chess library jar directly and add it to the classpath of your project manually

## Using the API

### Declaring a board model

Example of a ChessBoardModel object initialized and displayed in the standard output.

```java
public static void main(String... args) {
    ChessBoardModel board = new ChessBoardModel();

    board.setInitialPosition();

    System.out.println("Current position is:");
    System.out.println(board);
}
```

Will output:

```
Current position is:
WHITE KQkq
r n b q k b n r 
p p p p p p p p 
. . . . . . . . 
. . . . . . . . 
. . . . . . . . 
. . . . . . . . 
P P P P P P P P 
R N B Q K B N R
```

The position is displayed as multi-line ascii data:
* Which player has to play
* Four flags indicating if castling is still available at the four corners
* The board data:
  * Uppercase for white
  * Lowercase for black

### Making legal moves on a board

This example introduces the ChessRules interface.

Here the board model is accessed through the Position interface which is read-only. While the board model is internally mutable, it is recommended to avoid updating it in-place.

The following code is:
* Initializing a ChessRules instance
* Getting a read-only board in an initial position
* Iterating over all available moves
* Displaying the move and generating a new board model that will hold the new position after the move is played

The source code:

```java 
public static void main(String... args) throws IllegalMoveException {
    ChessRules rules = new ChessRulesImpl();
    ChessPosition position = rules.getInitialPosition();

    System.out.println("Current position:");
    System.out.println(position);

    System.out.println("Available moves:");

    for (ChessMovePath movePath : rules.getAvailableMoves(position)) {
        System.out.println("--> " + movePath);
        List<ChessBoardUpdate> updates = rules.getUpdatesForMove(position, movePath);

        ChessBoardModel afterMove = new ChessBoardModel();
        afterMove.setPosition(position);
        for ( ChessBoardUpdate update: updates) {
            update.apply(afterMove);
        }
        afterMove.nextPlayerTurn();

        System.out.println(afterMove);
    }
}
```

Note that the move is split in a collection of board updates that will breakdown the move in atomic board piece move/add/remove. This is handy to animate the board between moves.

Output:

```
Current position:
WHITE KQkq
r n b q k b n r 
p p p p p p p p 
. . . . . . . . 
. . . . . . . . 
. . . . . . . . 
. . . . . . . . 
P P P P P P P P 
R N B Q K B N R

Available moves:
--> ChessMovePath<g1:f3=queen>
BLACK KQkq
r n b q k b n r 
p p p p p p p p 
. . . . . . . . 
. . . . . . . . 
. . . . . . . . 
. . . . . N . . 
P P P P P P P P 
R N B Q K B . R

--> ChessMovePath<a2:a4=queen>
BLACK KQkq a4
r n b q k b n r 
p p p p p p p p 
. . . . . . . . 
. . . . . . . . 
P . . . . . . . 
. . . . . . . . 
. P P P P P P P 
R N B Q K B N R
```

### Replaying moves in PGN format

In the following example, we will be demonstrating the use of the PgnMarshaller. Since it has to be connected to the chess rules by autowiring, we will include our components in a Spring context.
PGN notation is a worldwide chess standard, here the moves are fetched from a static array. In a real life situation, these moves can be loaded from a file or a database.

```java
private static final String[] pgnMoves = {"e4", "e5", "Nf3", "Nc6", "Bb5"};

public static void main(String... args) throws IllegalMoveException, PgnMoveException {
    ApplicationContext appContext = new AnnotationConfigApplicationContext(
            ChessRulesImpl.class, PgnMarshallerImpl.class);

    ChessRules rules = appContext.getBean(ChessRules.class);
    PgnMarshaller marshaller = appContext.getBean(PgnMarshaller.class);
    ChessPosition position = rules.getInitialPosition();

    for (String pgn : pgnMoves) {
        ChessMovePath movePath = marshaller.convertPgnToMove(position, pgn);
        List<ChessBoardUpdate> updates = rules.getUpdatesForMove(position, movePath);

        ChessBoardModel afterMove = new ChessBoardModel();
        afterMove.setPosition(position);
        for (ChessBoardUpdate update : updates) {
            update.apply(afterMove);
        }
        afterMove.nextPlayerTurn();

        position = afterMove;
    }

    System.out.println(position);
}
```

Output:

```
BLACK KQkq
r . b q k b n r 
p p p p . p p p 
. . n . . . . . 
. B . . p . . . 
. . . . P . . . 
. . . . . N . . 
P P P P . P P P 
R N B Q K . . R
```

### Concise code using ChessHelper

The ChessHelper class provides methods allowing to directly apply a move on a given position, returning the destination position. This is used to avoid having to apply all board updates in a nested loop.

```java
@Autowired
private ChessRules chessRules;

@Autowired
private PgnMarshaller pgnMarshaller;

@Test
public void testGame() throws PgnMoveException, IllegalMoveException {
    String[] history = {"f3", "e5", "g4", "Qh4"};
    ChessPosition position = chessRules.getInitialPosition();

    for ( String pgnMove: history) {
        ChessMovePath path = pgnMarshaller.convertPgnToMove(position, pgnMove);
        position = ChessHelper.applyMove(chessRules, position, path);
    }

    System.out.println(position);
}
```

This will output:

```
Running org.alcibiade.chess.integration.ShortestGameTest
WHITE KQkq
r n b . k b n r 
p p p p . p p p 
. . . . . . . . 
. . . . p . . . 
. . . . . . P q 
. . . . . P . . 
P P P P P . . P 
R N B Q K B N R

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.856 sec
```

## License

LGPLv3 © [Yannick Kirschhoffer](http://www.alcibiade.org/)

[travis-image]: https://travis-ci.org/ChessCorp/chess-rules-java.svg?branch=master
[travis-url]: https://travis-ci.org/ChessCorp/chess-rules-java
