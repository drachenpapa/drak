# Contributing Guidelines

Thank you for considering a contribution to this project.

Please also read the [Code of Conduct](CODE_OF_CONDUCT.md).

## Project Scope

This project is considered feature-complete regarding core gameplay.

Contributions that are generally welcome:
- bug fixes
- documentation improvements
- dependency/tooling/CI maintenance (for example Renovate follow-ups)

Contributions that will usually not be accepted:
- new gameplay features
- changes to core game mechanics

If you want to propose a larger refactor, please open an issue first so we can align on scope before implementation.

---

## Bugs & Proposals

If you find a bug or want to propose a change, please [open an issue first](https://github.com/drachenpapa/drak/issues).
This helps avoid duplicate work or changes that don't fit the project.

For very small fixes (typos, wording, docs), a direct PR is fine.

If your report is security-related, please do not open a public issue. See the [Security Policy](SECURITY.md).

When reporting bugs, please include:
- what you expected
- what actually happened
- steps to reproduce the issue
- relevant logs, screenshots, or examples if available

---

## Commit Messages

Keep commit messages short and descriptive.

This project loosely follows semantic commit messages.

Examples:

```text
fix: prevent false collision after pause
docs: clarify controls in gameplay guide
build: update maven plugin versions
refactor: simplify collision manager flow
```

---

## Pull Requests

Please keep the following in mind:

- keep changes focused and minimal
- avoid unrelated formatting changes
- update docs/examples if needed
- make sure the project still builds/tests successfully

Draft PRs are welcome.
