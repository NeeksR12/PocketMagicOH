# 🃏 Pocket Magic OH! — Card Store Management System

A command-line application that manages trading card inventory, customer purchases, product bundles, and deck building for **Pocket Magic OH!** (PMO), a fictional trading card game. Built as part of the **Cardboard Kingdom 2026 IT Coding Competition**.

---

## Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Building the Project](#building-the-project)
  - [Running the Program](#running-the-program)
- [Usage & Command Reference](#usage--command-reference)
  - [Cards](#cards)
  - [Customers](#customers)
  - [Carts & Checkout](#carts--checkout)
  - [Bundles](#bundles)
  - [Decks](#decks)
  - [Reports](#reports)
- [Example Session](#example-session)
- [Software Architecture](#software-architecture)
  - [High-Level Overview](#high-level-overview)
  - [Design Patterns](#design-patterns)
  - [Database Schema](#database-schema)
- [Tech Stack](#tech-stack)
- [About Me](#about-me)
- [What I Learned](#what-i-learned)
- [Acknowledgements](#acknowledgements)

---

## About the Project

Pocket Magic OH! simulates a hobby store's back-end system for managing a trading card game launch. 
The program reads structured commands from standard input, processes them in sequence, and produces clear output — all 
without interactive prompts.

This project was developed for the **Cardboard Kingdom 2026 IT Coding Competition**, which was the 2026 Skills Ontario 
coding competition, where participants had a limited time window to build the best working prototype they could. 
The challenge is divided into progressive milestones; **Milestones 1 through 4 are fully implemented** in this version.

---

## Features

| Milestone | Feature                                                                                                                             |      Status       |
|:---------:|-------------------------------------------------------------------------------------------------------------------------------------|:-----------------:|
| 1 | **Card Inventory Management** — Create, update, and delete trading cards with element, rarity, price, and stock tracking            |    ✅ Complete     |
| 2 | **Customers & Shopping Carts** — Register customers, manage shopping carts, and process atomic checkouts with stock validation      |    ✅ Complete     |
| 3 | **Bundles** — Create composite product bundles (including nested bundles) that expand into individual cards at checkout             |    ✅ Complete     |
| 4 | **Deck Building** — Build named decks for customers and validate them against game legality rules (min 10 cards, max 4 card copies) |    ✅ Complete     |
| 5 | Card Queries — Search and filter cards with WHERE clauses, ordering, and limits                                                     | 🟨 Work in Progress |

---

## Getting Started

### Prerequisites

- **Java 24** or later — [Download OpenJDK](https://jdk.java.net/)
- **Apache Maven** — [Download Maven](https://maven.apache.org/download.cgi)

Verify your installations:

```bash
java --version
mvn --version
```

### Building the Project

Clone the repository and build with Maven:

```bash
git clone https://github.com/NeeksR12/PocketMagicOH.git
cd PocketMagicOH
mvn clean package
```

This compiles the source code and produces an executable `.jar` file in the `target/` directory.

### Running the Program

The program reads commands from **standard input** and writes results to **standard output**. There are no interactive prompts.

**Pipe a command file:**

```bash
java -jar target/PocketMagicOH-1.0-SNAPSHOT.jar < commands.txt
```

**Type commands manually** (enter **REPORT INVENTORY;** to signal end of commands):

```bash
java -jar target/PocketMagicOH-1.0-SNAPSHOT.jar
```

> **Note:** The program uses an embedded SQLite database (`PocketMagicOH.db`) that is created automatically in the working directory. Data **persists between runs** — cards, customers, carts, and decks you create will still be there next time. To start fresh, simply delete the `PocketMagicOH.db` file.

---

## Usage & Command Reference

All commands end with a semicolon (`;`). A command can span multiple lines — it is not complete until the semicolon is reached. Names and values are single tokens separated by whitespace (no spaces or semicolons in names). Commands are **case-sensitive**.

If a command cannot be completed, the program prints an error message and continues processing the next command.

### Cards

| Command | Description |
|---------|-------------|
| `CARD <name> CREATE element <value> rarity <value> price <value> stock <value>;` | Create a new card with the given attributes |
| `CARD <name> UPDATE <field> <value>;` | Update a single field (`element`, `rarity`, `price`, or `stock`) on an existing card |
| `CARD <name> DELETE;` | Delete a card (fails if the card is referenced in any cart, deck, or bundle) |

**Example:**

```
CARD EmberFox CREATE element fire rarity common price 2 stock 20;
CARD EmberFox UPDATE stock 50;
CARD EmberFox DELETE;
```

### Customers

| Command | Description |
|---------|-------------|
| `CUSTOMER <name> CREATE;` | Register a new customer |
| `CUSTOMER <name> DELETE;` | Remove a customer |

**Example:**

```
CUSTOMER ava CREATE;
CUSTOMER ava DELETE;
```

### Carts & Checkout

| Command                                                                         | Description |
|---------------------------------------------------------------------------------|-------------|
| `CART <customer> <cart_name> ADD <qty> <product> [AND <qty> <product> ...];`    | Add products (cards or bundles) to a customer's cart |
| `CART <customer> <cart_name> REMOVE <qty> <product> [AND <qty> <product> ...];` | Remove products from a customer's cart |
| `CART <customer> <cart_name> CLEAR;`                                            | Empty a customer's cart |
| `CHECKOUT <customer> <cart_name>;`                                              | Process the cart — validates stock, calculates total, deducts inventory, and clears the cart |

Checkout is **atomic**: if any item has insufficient stock, the entire checkout fails, inventory remains unchanged, and the cart is not cleared. The program will report which items are out of stock.

**Example:**

```
CART ava starters ADD 3 EmberFox AND 1 MoonTurtle;
CHECKOUT ava;
```

### Bundles

Bundles are groups of products sold together. A bundle can contain individual cards and/or other bundles, enabling nested compositions.

| Command | Description |
|---------|-------------|
| `BUNDLE <name> ADD <qty> <product> [AND <qty> <product> ...];` | Create or update a bundle with the specified products |
| `BUNDLE <name> REMOVE <qty> <product> [AND <qty> <product> ...];` | Remove products from a bundle |
| `BUNDLE <name> CLEAR;` | Empty all products from a bundle |
| `BUNDLE <name> DELETE;` | Delete a bundle (fails if referenced in a cart or another bundle) |

Bundles are **expanded into individual cards** during checkout. The bundle's price is calculated dynamically from its contents.

**Example:**

```
BUNDLE fire_starter ADD 4 EmberFox AND 2 SparkSprite AND 5 FlameToken;
BUNDLE deluxe_fire ADD 2 fire_starter AND 1 EmberFox;
CART ava ADD 1 deluxe_fire;
CHECKOUT ava;
```

### Decks

Customers can build named decks from cards in the store inventory.

| Command | Description |
|---------|-------------|
| `DECK <customer> <deck_name> ADD <qty> <card>;` | Add cards to a customer's deck |
| `DECK <customer> <deck_name> REMOVE <qty> <card>;` | Remove cards from a customer's deck |
| `DECK <customer> <deck_name> CLEAR;` | Empty a deck |
| `CHECK_DECK <customer> <deck_name>;` | Check whether a deck meets the legality rules |

**Deck Legality Rules:**

- The deck must contain **at least 10 total cards**
- No single card may appear **more than 4 times**
- Every card in the deck must exist in the store inventory

**Example:**

```
DECK ava fire_deck ADD 4 EmberFox;
DECK ava fire_deck ADD 3 SparkSprite;
DECK ava fire_deck ADD 3 FlameToken;
CHECK_DECK ava fire_deck;
```

### Reports

| Command | Description |
|---------|-------------|
| `REPORT INVENTORY;` | Print the current stock of all cards in the inventory |

---

## Example Session

**Input:**

```
CARD EmberFox CREATE element fire rarity common price 2 stock 20;
CARD SparkSprite CREATE element fire rarity uncommon price 4 stock 10;
CARD FlameToken CREATE element fire rarity token price 0 stock 999;
CARD MoonTurtle CREATE element water rarity rare price 8 stock 5;
CARD StoneGolem CREATE element earth rarity uncommon price 5 stock 8;

CUSTOMER ava CREATE;
CUSTOMER ben CREATE;

BUNDLE fire_starter ADD
    4 EmberFox AND
    2 SparkSprite AND
    5 FlameToken;

CART ava starters ADD 2 fire_starter AND 3 EmberFox;
CHECKOUT ava starters;

DECK ava fire_deck ADD 4 EmberFox;
DECK ava fire_deck ADD 3 SparkSprite;
DECK ava fire_deck ADD 3 FlameToken;
CHECK_DECK ava fire_deck;

REPORT INVENTORY;
```

**Output:**

```
card EmberFox added
card SparkSprite added
card FlameToken added
card MoonTurtle added
card StoneGolem added

customer ava created
customer ben created

bundle fire_starter updated

ava cart starters updated
ava total 38

deck fire_deck updated
deck fire_deck updated
deck fire_deck updated

ava fire_deck legal

INVENTORY
EmberFox 9
SparkSprite 6
FlameToken 989
MoonTurtle 5
StoneGolem 8
```

---

## Software Architecture

### High-Level Overview

The application follows a **Layered Architecture** with clear separation between four layers:

```
┌─────────────────────────────────────────────┐
│          StoreFront (CLI / Entry Point)     │  ← Reads input, dispatches commands
├─────────────────────────────────────────────┤
│          commands Package                   │  ← Command Pattern: parse & execute
├──────────────────────┬──────────────────────┤
│  entities Package    │  databases Package   │  ← Domain model & persistence
│  (Domain Objects)    │  (Repositories &     │
│                      │   Change Tracking)   │
├──────────────────────┴──────────────────────┤
│          SQLite Database (JDBC)             │  ← Embedded relational storage
└─────────────────────────────────────────────┘
```

**Data flows in one direction:**

1. **Bootstrap** — `StoreFront` opens an SQLite connection and hydrates in-memory models from the database via repository classes.
2. **Input Ingestion** — Raw text from `stdin` is tokenized into semicolon-delimited command strings.
3. **Command Creation** — The `CommandType` enum acts as a factory, mapping keywords to concrete `Command` subclasses.
4. **Two-Phase Execution** — Each command first validates (`parse()`), then mutates state (`run()`). If validation fails, no state is changed.
5. **Persistence** — On shutdown, only entities marked as dirty or deleted are written back to the database in a single atomic transaction.

### Design Patterns

| Pattern | Where It's Used | Why |
|---------|----------------|-----|
| **Command** | `Command` abstract class + all `*CMD` subclasses | Encapsulates each user action as an object with independent validation and execution |
| **Factory Method** | `CommandType` enum | Each enum constant instantiates its own `Command` subclass, eliminating long switch blocks |
| **Composite** | `Bundle` containing `Product`s (including other `Bundle`s) | Enables recursive nesting of bundles; `toItems()` flattens the tree into leaf cards for checkout |
| **Repository** | `InventoryRepository`, `CustomersRepository` | Abstracts all SQL operations behind clean read/write interfaces |
| **Unit of Work** | `TrackedCollection<T>` | Tracks `dirty` and `deleted` entities so only changed records generate SQL, not full-table rewrites |

### Database Schema

The SQLite database (`PocketMagicOH.db`) contains 9 tables:

```
products ──┬── cards         (1:1, product details for cards)
           └── bundles       (1:1, product details for bundles)
                 └── bundle_items  (1:N, products inside a bundle)

customers ──┬── carts         (1:N, shopping carts per customer)
            │    └── cart_items    (1:N, products inside a cart)
            └── decks         (1:N, decks per customer)
                 └── deck_cards   (1:N, cards inside a deck)
```

---

## Tech Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 24 | Core language (using modern features: pattern matching, switch expressions, text blocks) |
| Apache Maven | 4.0.0 | Build automation and dependency management |
| SQLite | via `sqlite-jdbc 3.49.1.0` | Embedded relational database for persistent storage |
| IntelliJ IDEA | — | Development environment |

---

## About Me

Hi, I'm Nico Rotella! I'm a student studying mechatronics engineering at the University of Waterloo passionate about
robotics, autonomy, and software development.

Connect with me on [LinkedIn](https://www.linkedin.com/in/nico-rotella/).

---

## What I Learned

In working on this project, I learned and improved in many areas.
The most major of which is utilizing SQLite to store persistable data in a database file.
Other areas I improved and taught myself more about are the applications of polymorphism (especially through 
implementing interfaces), generics in classes and interfaces, and usage of enum inheritors.

---

## Acknowledgements

A big thank you to my Dad for providing valuable insights as well as being my biggest role model.
Also thank you to my high school computer science teacher, Mrs. Schembri, for nominating me to take part in this competition.

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

