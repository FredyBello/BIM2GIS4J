---
title: markmap
markmap:
  colorFreezeLevel: 2
  maxWidth: 300
---

## Links

- [Website](https://markmap.js.org/)
- [GitHub](https://github.com/gera2ld/markmap)

### Blocks

- Now we can wrap very very very very long text with the `maxWidth` option
- Ordered list
  1. item 1
  - aaa
  - bbb
  2. item 2

### Code

```java
static double getBalanceWithSwitchPattern(Account account) {
    double result = 0;
    switch (account) {
        case null -> throw new RuntimeException("Oops, account is null");
        case SavingsAccount sa -> result = sa.getSavings();
        case TermAccount ta -> result = ta.getTermAccount();
        case CurrentAccount ca -> result = ca.getCurrentAccount();
        default -> result = account.getBalance();
    };
    return result;
}
```
### Others

| Products | Price |
|-|-|
| Apple | 4 |
| Banana | 2 |
| Orange | 12 |

![](https://markmap.js.org/favicon.png)