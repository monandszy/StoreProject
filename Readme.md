# Project Overview

**Written:** 2024.02.04 - 2024.02.06

This is a practice project focused on using Spring JDBC within a layered architecture.

## Key Features
- **Generic Abstraction Layer:** I have instinctively created a generic abstraction layer (CRUDRepository<T>) to minimize boilerplate. This allowed for experimentation with various methods for writing queries and mapping objects, to find the most optimal one.
- **Entities:** The project uses common relations between `Customer`, `Producer`, `Product`, `Purchase`, and `Opinion`.