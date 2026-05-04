# JPA Relationships Mastery Assignment

**Domain:** Online Bookstore Management System
**Stack:** Spring Boot 3.x + Spring Data JPA + H2 (in-memory) or PostgreSQL
**Level:** Intermediate
**Estimated time:** 8–12 hours (split across 3–4 sittings)

---

## Why this assignment

You will model a real-world bookstore where every kind of JPA relationship shows up naturally — a customer has one profile (one-to-one), an author writes many books (one-to-many / many-to-one), books span multiple categories and tags (many-to-many), and orders contain line items (one-to-many with extra columns on the join). By the end you should be able to look at any business domain and decide cardinality, ownership, cascade, and fetch strategy without guessing.

---

## Learning objectives

By completing this assignment you will be able to:

1. Choose the right annotation for any cardinality: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`.
2. Decide which side owns a bidirectional relationship and use `mappedBy` correctly.
3. Apply `cascade` types (`PERSIST`, `MERGE`, `REMOVE`, `ALL`) deliberately rather than as a default.
4. Pick `FetchType.LAZY` vs `FetchType.EAGER` and justify the choice.
5. Use `orphanRemoval = true` and explain how it differs from `CascadeType.REMOVE`.
6. Define join columns and join tables explicitly when the defaults are wrong.
7. Map a many-to-many with extra attributes by promoting it to two one-to-many relationships through a join entity.
8. Write JPQL and Spring Data derived queries that traverse relationships.
9. Recognize and fix the N+1 select problem using `JOIN FETCH` or `@EntityGraph`.

---

## Project setup

1. Generate a Spring Boot 3 project from start.spring.io with: `Spring Web`, `Spring Data JPA`, `H2 Database`, `Validation`, `Lombok` (optional).
2. Package structure:
   ```
   com.bookstore
     ├─ entity      // JPA entities go here
     ├─ repository  // Spring Data interfaces
     ├─ service     // Business logic
     ├─ controller  // REST endpoints (Part 5)
     └─ dto         // request/response objects
   ```
3. In `application.properties` enable:
   ```
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.orm.jdbc.bind=TRACE
   ```
   You will read SQL output throughout — reading the queries Hibernate emits is the fastest way to learn this material.

---

## Part 1 — One-to-One: Customer and CustomerProfile

A `Customer` has authentication data (email, passwordHash). A `CustomerProfile` stores optional extras (bio, phone, dateOfBirth, avatarUrl). A profile must always belong to exactly one customer; a customer may exist before a profile is created.

**Tasks**

1. Create both entities. The customer should not have its profile fields embedded — it must be a separate table.
2. Make the relationship bidirectional. Decide which side owns the foreign key and justify your choice in a comment above the field.
3. Configure cascade so that saving a `Customer` with a new profile persists both in one call, and deleting a customer deletes the profile.
4. Use lazy fetching and prove it: write a test that loads a customer by id and asserts that the profile is not yet initialized (hint: `Hibernate.isInitialized(...)` or check the SQL log).
5. Write a service method `attachProfile(Long customerId, ProfileDto dto)` that handles both creating a new profile and updating the existing one.

**Acceptance criteria**

- Two tables, one foreign key.
- Saving a `Customer` with a profile produces exactly one `INSERT` per table.
- Deleting a customer with a profile produces deletes for both.
- A round-trip read of `Customer` without touching `getProfile()` issues a single SELECT.

---

## Part 2 — Many-to-One / One-to-Many: Author and Book

Each `Book` has exactly one `Author`. An `Author` has many books. A book has: title, isbn (unique), publishedYear, priceCents, stockCount.

**Tasks**

1. Model the relationship bidirectionally. The `Book` side owns the foreign key. The `Author.books` side uses `mappedBy`.
2. Add helper methods `Author.addBook(Book b)` and `Author.removeBook(Book b)` that keep both sides of the relationship in sync. Explain in a comment why this matters.
3. Use `orphanRemoval = true` on `Author.books` so that removing a book from the list deletes it from the database. Then write a failing-then-passing test that proves it works.
4. Compare `orphanRemoval = true` vs `CascadeType.REMOVE` in 3–5 sentences in a `NOTES.md` file. When does each matter?
5. Spring Data: create `BookRepository` with these query methods (no JPQL — use derived query naming):
    - `findByAuthorId(Long authorId)`
    - `findByAuthor_NameContainingIgnoreCase(String fragment)`
    - `findByPriceCentsBetween(int min, int max)`
    - `countByAuthorId(Long authorId)`
6. Then add one **JPQL** query: `@Query("select b from Book b where b.publishedYear = :year and b.author.country = :country")`.

**Acceptance criteria**

- The `books` table has an `author_id` column with a non-null foreign key.
- Removing a book via `author.getBooks().remove(book)` followed by `save(author)` issues a `DELETE FROM books`.
- Loading an author does not eagerly fetch books unless you call `getBooks()`.

---

## Part 3 — Many-to-Many: Book ↔ Category and Book ↔ Tag

A book belongs to one or more categories (e.g. *Fiction*, *Science*) and has zero or more tags (e.g. *bestseller*, *award-winner*). Categories and tags exist independently of books.

**Tasks**

1. Model `Book ↔ Category` as a classic `@ManyToMany`. Use `@JoinTable` to **explicitly name** the join table `book_categories` and its two columns `book_id`, `category_id`. Do not let Hibernate guess.
2. Make the relationship bidirectional and choose an owner. Explain in a comment which side is the owner and why removing a category from a book vs removing a book from a category behaves differently.
3. Model `Book ↔ Tag` similarly but **unidirectional from Book**. Tags don't need to know about books.
4. Write tests that:
    - Add a category to a book and assert the join row appears.
    - Remove a category from a book and assert the join row is gone but both `Book` and `Category` survive.
    - Delete a `Book` that has 3 categories and assert that all 3 join rows disappear without deleting the categories themselves.
5. Trap to recognize: try setting `cascade = CascadeType.ALL` on the `@ManyToMany` and then deleting a book. What happens to the categories? Document the result, then revert. This is one of the most common JPA bugs — make sure you can spot it.

**Acceptance criteria**

- A `book_categories` table exists with exactly two columns and a composite primary key.
- Removing the link does not remove either entity.
- The `Tag` side has no `books` collection.

---

## Part 4 — Join entity: Order, OrderItem, Book

`Order` has many `OrderItem`s. Each `OrderItem` references a `Book` and stores `quantity` and `unitPriceCentsAtPurchase` (price is captured at order time, not read live). This is a many-to-many between `Order` and `Book` with extra columns — so you must promote the join into its own entity.

**Tasks**

1. Create `Order`, `OrderItem`, and reuse `Book`. `OrderItem` has a `@ManyToOne` to both `Order` and `Book`.
2. `Order.items` is a `@OneToMany(mappedBy = "order")` with `cascade = {PERSIST, MERGE}` and `orphanRemoval = true`.
3. Add a derived `Order.totalCents` computed in Java (not stored). Cover it with a unit test.
4. Implement `OrderService.placeOrder(Long customerId, List<OrderLineDto> lines)` that:
    - Loads books by id in a single query (hint: `findAllById`).
    - Validates stock, decrements `Book.stockCount`, builds the order graph, and saves once.
    - Throws a custom `OutOfStockException` if any line exceeds available stock — and rolls back.
5. Add `Order.status` as an enum (`PENDING`, `PAID`, `SHIPPED`, `CANCELLED`) using `@Enumerated(EnumType.STRING)`. Explain in `NOTES.md` why `STRING` is preferred over `ORDINAL`.

**Acceptance criteria**

- Placing an order with 3 line items issues at most: 1 select for books by id, N updates for stock, 1 insert for order, 3 inserts for items. (Look at the SQL log.)
- A failed order leaves stock unchanged.
- Removing an item from `order.getItems()` and saving deletes the row.

---

## Part 5 — Reviews and the N+1 problem

Add a `Review` entity with `@ManyToOne` to both `Customer` and `Book`, plus `rating` (1–5) and `comment`.

**Tasks**

1. Add `Book.reviews` as `@OneToMany(mappedBy = "book", fetch = FetchType.LAZY)`.
2. Write an endpoint `GET /books` that returns a list of books with each book's review count and average rating. Implement it naïvely first (load books, then loop and call `book.getReviews()`).
3. Run it with 50 books seeded and look at the SQL log. You should see the classic 1 + N queries. Confirm and document the count.
4. Fix it using each of these techniques in separate methods so you understand the trade-offs:
    - JPQL with `JOIN FETCH`.
    - `@EntityGraph(attributePaths = "reviews")` on the repository method.
    - A projection / DTO query that selects `count(r)` and `avg(r.rating)` directly without loading reviews.
5. In `NOTES.md`, write 4–6 sentences on which approach you would ship and why.

**Acceptance criteria**

- The naïve version emits 1 + N queries for N books.
- Each fixed version emits at most 2 queries.
- The DTO projection version does not load `Review` entities into the persistence context at all.

---

## Part 6 — Extension challenges (pick at least two)

1. **Soft delete.** Add `deletedAt` to `Book` and use a Hibernate `@SQLDelete` + `@Where` (or `@SQLRestriction` in Hibernate 6) so that deleted books are filtered out of every query without rewriting the queries.
2. **Auditing.** Use Spring Data's `@CreatedDate` and `@LastModifiedDate` on every entity via a `BaseEntity` with `@MappedSuperclass`.
3. **Inheritance.** Split `Book` into `PhysicalBook` and `Ebook` with `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` and a discriminator column. Add fields specific to each subtype.
4. **Composite key.** Add a `Wishlist` whose primary key is `(customer_id, book_id)`. Use `@Embeddable` + `@EmbeddedId`.
5. **Optimistic locking.** Add `@Version` to `Book` and write a test that simulates two concurrent stock decrements — one should fail with `OptimisticLockingFailureException`.

---

## Deliverables

1. The Spring Boot project, runnable with `./mvnw spring-boot:run` (or Gradle equivalent).
2. A `data.sql` (or `CommandLineRunner`) that seeds: 5 authors, 50 books, 8 categories, 12 tags, 20 customers, 100 reviews.
3. A `tests/` directory with `@DataJpaTest` slice tests for each part. Aim for at least one test per acceptance criterion.
4. A `NOTES.md` answering the reflection prompts asked above and the questions below.

---

## Reflection prompts (answer in NOTES.md)

1. In which of your relationships is the foreign key owned by the "many" side, and in which by the "one" side? Why?
2. Where did you use `cascade = ALL` and where did you avoid it? What rule of thumb did you arrive at?
3. What's the difference between `orphanRemoval = true` and `cascade = REMOVE`? Give an example where they behave differently.
4. Why is `FetchType.EAGER` on a `@OneToMany` almost always a mistake?
5. Which of your repository queries triggered the N+1 problem? How did you discover it, and what fixed it?
6. If you could redesign one entity from scratch, which would it be and why?

---

## Self-grading rubric

| Area | Beginner (1) | Solid (2) | Strong (3) |
|---|---|---|---|
| Cardinality choices | Compiles, runs | Bidirectional sides correctly chosen | Justified in writing |
| Cascade & orphanRemoval | Default everywhere | Used deliberately | Documented trade-offs |
| Fetch strategy | Default everywhere | LAZY by default, EAGER never on collections | N+1 fixed with EntityGraph/JOIN FETCH |
| Join tables | Auto-named | Explicitly named with `@JoinTable` | Composite keys understood |
| Tests | Happy path only | One per acceptance criterion | Includes failure / rollback paths |
| SQL awareness | Doesn't read logs | Can identify queries per operation | Can predict query count before running |

Aim for a 2 across the board before claiming you are proficient. Push to 3 in at least two areas.

---

## Recommended order of work

1. Read this whole document first, then do Parts 1 and 2 in one sitting.
2. Stop and read the Hibernate-emitted SQL for everything you wrote so far. If anything surprises you, don't move on.
3. Parts 3 and 4 in the next sitting.
4. Part 5 last — N+1 only clicks once you have something non-trivial to break.
5. Pick extension challenges based on what tripped you up.

Good luck — the SQL log is your best teacher on this one.
