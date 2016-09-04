# Combinations
#### A utility for generating combinations and permutations of input collections with relational filtering

----

## Usage

### Combinator

#### Simple example

```java
new Combinator().chooseTwo('a', 'b', 'c').permuteTwo(1, 2, 3).stream()

[a, b, 1, 2], [a, b, 1, 3], [a, b, 2, 1], [a, b, 2, 3], [a, b, 3, 1], [a, b, 3, 2],
[a, c, 1, 2], [a, c, 1, 3], [a, c, 2, 1], [a, c, 2, 3], [a, c, 3, 1], [a, c, 3, 2],
[b, c, 1, 2], [b, c, 1, 3], [b, c, 2, 1], [b, c, 2, 3], [b, c, 3, 1], [b, c, 3, 2]
```

#### Advanced example

```java
new Combinator().permuteThree(
    t('!', '*'),
    new Combinator().chooseTwo('x', 'y', 'z')
).stream()

[!, *, x, y, x, z], [!, *, x, y, y, z], [!, *, x, z, x, y],
[!, *, x, z, y, z], [!, *, y, z, x, y], [!, *, y, z, x, z],

[x, y, !, *, x, z], [x, y, !, *, y, z], [x, y, x, z, !, *],
[x, y, x, z, y, z], [x, y, y, z, !, *], [x, y, y, z, x, z],

[x, z, !, *, x, y], [x, z, !, *, y, z], [x, z, x, y, !, *],
[x, z, x, y, y, z], [x, z, y, z, !, *], [x, z, y, z, x, y],

[y, z, !, *, x, y], [y, z, !, *, x, z], [y, z, x, y, !, *],
[y, z, x, y, x, z], [y, z, x, z, !, *], [y, z, x, z, x, y]
```

### Relation filter

#### Simple Example

```java
new RelationFilter(
    new Combinator().permuteOne(true, false).permuteOne(1, 2, 3).permuteOne('a', 'b', 'c', 'd'),
    ALL_PAIRS(3)
).get();

[true, 2, a]
[false, 1, a]
[false, 1, b]
[false, 1, c]
[false, 1, d]
[true, 2, c]
[true, 3, b]
[false, 2, d]
[false, 3, a]
[true, 2, b]
[true, 3, c]
[true, 1, c]
[true, 3, d]
```

#### Advanced example

```java
final Combinator advanced_comb = new Combinator()
    .chooseOne(
        t(true, false, false),
        new Combinator()
            .chooseOne(false)
            .chooseOne(true, false)
            .chooseOne(true, false)
    )
    .chooseOne(
        t(true, false, false),
        new Combinator()
            .chooseOne(false)
            .chooseOne(true, false)
            .chooseOne(true, false)
    )
    .chooseOne(true, false);

new RelationFilter(advanced_comb, t(0, 1, 2, 6), t(3, 4, 5, 6)).get();

[false, true, true, true, false, false, true]
[false, false, false, false, true, false, true]
[false, false, true, true, false, false, true]
[true, false, false, false, true, true, true]
[false, false, false, false, false, true, true]
[false, true, false, true, false, false, true]
[false, false, true, false, true, false, false]
[false, false, true, true, false, false, false]
[false, false, false, false, true, false, false]
[true, false, false, false, false, false, false]
[false, true, true, false, false, true, false]
[false, false, false, false, false, false, true]
[true, false, false, false, true, true, false]
[false, true, false, false, false, true, false]
```

## How to build

Use Maven.

Alternatively you can just run javac 1.8+ on everything and jar the resulting goo.

## How to submit bug reports / feature requests

Just use GitHub's integrated tracker.

Please include JVM details (version) and a failing test case for bugs.

For feature requests, please include a scenario where the hypothetical feature improves things.

## How to improve Combinations

If you'd like to contribute to Combinations, you'll first need to
[sign the CLA](https://cla-assistant.io/TaikunEngineering/combinations).

Before you start coding, create a bug/feature-request in the tracker.
 
Iff I label the issue "Pull candidate", then I will consider pull requests that address the issue.

You are always welcome (with signed CLA) to just attach snippets to the bug report.

## License

Combinations is freely available under the Apache License v2.

## Misc questions

Supported systems?

- I develop and test using Oracle 1.8 JDKs on Windows 10 and RHEL 7 on AMD64
- However this should work on any compliant JVM
- I will reproduce/test on Debian/Ubuntu if necessary
- All other platforms have [paid support](support@taikun.engineering) available

Paid support?

- [Available](support@taikun.engineering), including custom branches, 3rd party code integrations, faster response time,
private bug tracker
