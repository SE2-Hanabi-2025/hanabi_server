# Hanabi Game WebSocket API

This document describes the WebSocket API for the Hanabi game server, which allows for real-time game actions like playing cards, discarding cards, and giving hints.

## Connection

Connect to the WebSocket endpoint with the following URL format:

```
ws://{server-address}/ws/game?lobbyId={lobbyId}&playerId={playerId}
```

Replace `{server-address}` with your server's address, `{lobbyId}` with your game's lobby ID, and `{playerId}` with your player ID.

## Message Format

All messages are JSON objects. The client sends action requests to the server, and the server responds with action results and game state updates.

### Client-to-Server Messages

#### Play a Card

```json
{
  "action": "PLAY",
  "lobbyId": "LOBBY123",
  "playerId": 1,
  "cardIndex": 2
}
```

#### Discard a Card

```json
{
  "action": "DISCARD",
  "lobbyId": "LOBBY123",
  "playerId": 1,
  "cardIndex": 2
}
```

#### Give a Hint

```json
{
  "action": "HINT",
  "lobbyId": "LOBBY123",
  "playerId": 1,
  "toPlayerId": 2,
  "hintType": "COLOR",
  "hintValue": "RED"
}
```

For number hints, use:

```json
{
  "action": "HINT",
  "lobbyId": "LOBBY123",
  "playerId": 1,
  "toPlayerId": 2,
  "hintType": "VALUE",
  "hintValue": "5"
}
```

### Server-to-Client Messages

#### Action Result

After an action is performed, the server sends an action result:

```json
{
  "type": "SUCCESS",
  "message": "Card played successfully",
  "details": {
    "cardPlayed": {
      "color": "BLUE",
      "value": 3
    },
    "newCard": {
      "color": "RED",
      "value": 1
    }
  }
}
```

If there's an error, the result might look like:

```json
{
  "type": "INVALID_MOVE",
  "message": "Cannot play card: not your turn"
}
```

#### Game State Update

After every action, all connected players receive an updated game state:

```json
{
  "players": [
    {
      "id": 1,
      "name": "Player 1",
      "hand": [
        {
          "hints": {
            "colors": ["RED"],
            "values": []
          }
        },
        {
          "hints": {
            "colors": [],
            "values": [5]
          }
        }
      ]
    }
  ],
  "currentPlayerId": 2,
  "hintTokens": 7,
  "lifeTokens": 3,
  "deck": {
    "remainingCards": 40
  },
  "playedCards": {
    "RED": [1],
    "BLUE": [1, 2, 3],
    "GREEN": [],
    "YELLOW": [1],
    "WHITE": []
  },
  "discardPile": [
    {
      "color": "GREEN",
      "value": 1
    }
  ],
  "gameOver": false
}
```

## Example Client

A JavaScript client implementation is provided in `websocket-client.js`. See the file for usage examples.

## Error Handling

If there's an error with a message format or an invalid action, the server will respond with an error message:

```json
{
  "error": "Invalid message format"
}
```

## Disconnection

The WebSocket connection may be closed by the server in these cases:
- Missing lobbyId or playerId in the connection parameters
- Client inactivity timeout
- Game ending
- Server shutdown

The close event will include a status code and reason that can be handled by your client application.
