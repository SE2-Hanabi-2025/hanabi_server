# Hanabi Server

This is the backend server for the **Hanabi** multiplayer game, built using **Spring Boot**.

## About Hanabi

Hanabi is a cooperative card game where players work together to create a perfect fireworks display. Players can see other players' cards but not their own, requiring strategic communication through hints to successfully play cards in the right order.

## Requirements

- **Java 17+** (or a compatible version)
- **Maven** for building the project

## Getting Started

1. Clone the repository
2. Build the project with Maven:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Game Features

- **Lobby System**: Create and join game lobbies
- **Game Actions**: Play cards, discard cards, and give hints
- **Game State Management**: Tracks game progression, scores, and end conditions
- **Hint System**: Communicate with other players through color and value hints

## API Endpoints

### Lobby Management

- **`/create-lobby`** (GET)
  - **Description**: Creates a new lobby and returns its unique ID.
  - **Response**: Text containing the lobby ID.

- **`/join-lobby/{lobbyId}`** (GET)
  - **Description**: Joins an existing lobby with the specified ID.
  - **Parameters**: `lobbyId` (path), `playerName` (query)
  - **Response**: Player ID or error message.

### Game Actions

- **`/api/game/{lobbyId}/status`** (GET)
  - **Description**: Gets the current game status for a specific player.
  - **Parameters**: `lobbyId` (path), `playerId` (query)
  - **Response**: JSON with game state information.

- **`/api/game/{lobbyId}/play`** (POST)
  - **Description**: Plays a card from a player's hand.
  - **Parameters**: `lobbyId` (path), `playerId` & `cardPosition` (query)
  - **Response**: Action result with success/failure information.

- **`/api/game/{lobbyId}/discard`** (POST)
  - **Description**: Discards a card from a player's hand.
  - **Parameters**: `lobbyId` (path), `playerId` & `cardPosition` (query)
  - **Response**: Action result with success/failure information.

- **`/api/game/{lobbyId}/hint`** (POST)
  - **Description**: Gives a hint to another player about their cards.
  - **Parameters**: `lobbyId` (path), `fromPlayerId`, `toPlayerId`, `hintType`, `value` (query)
  - **Response**: Action result with success/failure information.

## Game Rules

- Players take turns performing one of three actions: play a card, discard a card, or give a hint
- Players can't see their own cards but can see others' cards
- Hints cost hint tokens, which are replenished when cards are discarded
- Cards must be played in ascending order (1-5) by color
- The game ends when:
  - All cards are successfully played (win)
  - The deck is empty and each player has taken one more turn (partial win, scored)
  - Three wrong cards have been played (loss)

## Technical Information

- The server uses **Spring Boot** for REST API endpoints
- **Swagger UI** is available for API documentation and testing
- The server listens on **`localhost:8080`** by default
- JSON is used for data exchange between client and server

## Development

- **Testing**: Tests are available in the `src/test` directory
- **Logging**: Game actions and events are logged for debugging and analysis
- **Documentation**: API endpoints are documented with Swagger annotations
