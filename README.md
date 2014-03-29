MTG Proxy Generator
===================

Given a list of magic cards, create a file (html or pdf) of the card images.

[![Build Status](https://travis-ci.org/jvalentini/mtg-proxy-pdf.svg?branch=master)](https://travis-ci.org/jvalentini/mtg-proxy-pdf)

TODO:
- Allow user to determine output format (pdf or html)
- Write a protocol/interface for images->html and images->pdf so it can easily dispatch.
- Add html template and css file.
- Make it generic enough so you can easily add another site to pull images from.
- Handle empty lines in text file
- Handle whitespace after card names
- Handle image urls that can't be found
- It should create a minimal decklist. If duplicate card names are seen, it should combine them.