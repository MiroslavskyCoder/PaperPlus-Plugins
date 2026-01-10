# Economy Plugin

Full-featured economy system with banks, transactions, and interest.

## Features

- **Account Management**: Personal balance tracking and bank accounts
- **Transactions**: Player-to-player payments with configurable fees
- **Banking System**: Deposit/withdraw with interest calculation
- **Balance Top**: Leaderboard of richest players with auto-update
- **Admin Commands**: Give, take, set, or reset player balances
- **Currency Customization**: Configure name, symbol, and starting balance

## Commands

| Command | Usage | Permission |
|---------|-------|-----------|
| `/balance [player]` | Check balance | economy.use |
| `/pay <player> <amount>` | Pay another player | economy.use |
| `/baltop [page]` | View richest players | economy.use |
| `/bank <deposit\|withdraw\|balance> [amount]` | Bank operations | economy.use |
| `/eco <give\|take\|set\|reset> <player> <amount>` | Admin commands | economy.admin |

## Permissions

- `economy.use` - Use economy system (default: true)
- `economy.admin` - Admin economy commands (default: op)
- `economy.bypass.transaction-fee` - Bypass payment fees (default: op)

## Configuration

```yaml
currency:
  name: Coin
  plural: Coins
  symbol: "$"
  starting-balance: 1000.0
  
transactions:
  min-amount: 0.01
  max-amount: 1000000.0
  fee-enabled: true
  fee-percentage: 2.0
  
bank:
  enabled: true
  interest-enabled: true
  interest-rate: 0.5 # 0.5% per day
  interest-interval: 86400 # seconds
  max-balance: 10000000.0
```

## Storage

Supports both YAML (file-based) and MySQL storage backends. Configure in config.yml.

## Version

0.1.0

## Author

WebX
