# Shop Plugin

Advanced shop system with GUI, admin shops, and transaction management.

## Features

- **Shop Creation**: Admin shops with `/adminshop create <name>`
- **GUI Interface**: Inventory-based shop menu
- **Buy & Sell**: Players can purchase and sell items
- **Shop Management**: Create, delete, edit, and list shops
- **Item Pricing**: Configure buy and sell prices per item
- **Stock Management**: Track item inventory and availability

## Commands

| Command | Usage | Permission |
|---------|-------|-----------|
| `/shop [name]` | Open shop or shop list | shop.use |
| `/adminshop create <name>` | Create shop | shop.admin |
| `/adminshop delete <name>` | Delete shop | shop.admin |
| `/adminshop list` | List all shops | shop.admin |
| `/adminshop edit <name>` | Edit shop | shop.admin |

## Permissions

- `shop.use` - Use shops (default: true)
- `shop.create` - Create shops (default: op)
- `shop.admin` - Manage all shops (default: op)

## Configuration

```yaml
shop:
  max-shops: 100
  max-items-per-shop: 54
  
gui:
  title: "Shop Menu"
  rows: 6
  
item-cost:
  min: 0.01
  max: 1000000.0
```

## Integration

- **Economy Plugin**: Uses Economy plugin for player payments
- **Vault**: Ready for Vault economy integration

## Version

0.1.0

## Author

WebX
