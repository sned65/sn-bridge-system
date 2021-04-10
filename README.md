# Introduction

The program is designed to facilitate the description
of bridge bidding systems. The syntax is based on the Markdown
markup language, which is enriched with bridge-specific elements.

# Installation

## Prerequisites

Make sure you have the Java 11 JDK installed.
To check, open the terminal and type:
```
java -version
```
If you don't have it installed, download Java from, for example,
<a href="https://adoptopenjdk.net/">AdoptOpenJDK</a>.

## Run the application

Download and unzip the `bridge-system-<version>.zip` file on the target folder.
Then, run the script in the bin directory.
The name of the script is `bridge-system`, and it comes in two versions,
a bash shell script, and a Windows `.bat` script.

To create both HTML and PDF from the description in `MySystem.bs`:
```
bridge-system MySystem.bs --html --pdf
```

To get all available options:
```
bridge-system --help
```

# BS-formatting

To get a complete description of the bs-syntax as html:
```
bridge-system --help-bs-syntax
```

Alternatively, click on the question mark at the top-right corner of
the html-page produced by `bridge-system MySystem.bs --html`.

## Fragment of bs-file

```
# Openings

Mostly natural system with strong NT and 5-card majors.

\bids{
1♣, 1\d | 12-21, 3+; with 3-3 in the minors – always 1♣;
        | with 4-4 in the minors or 5♣-4♦ – use judgement.
1♥, 1♠ | 11-21, 5+
1NT    | 15-17, BAL
}


# 1♣, 1♦ Openings

## Responses with unpassed hand

\bdn{
1♣ (P) ?
}

*Walsh: with less than 12 points, holding 4+ diamonds and a 4-card major,
responder should bid the major first.*

\bids{
1♦     | 6+ HCP, 4+, (Walsh)
1♥/♠   | 6+ HCP, 4+♥/♠
1NT    | 6-10 HCP
2♣     | 9+ HCP, 4+♣, F1
}

**Goals in Inverted Minor auctions**:

- Stopping in 3m when opener has a minimum and responder only a game-invitation.
- Getting to 3NT from the right side.
- Avoiding bad 3NT contracts.
- Getting to good slams.
```