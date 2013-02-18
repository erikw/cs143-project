# Compile a LaTeX file to different output formats.
# TODO rewrite to a real Makefile.
NAME=report

.PHONY: all clean compile help wc

# target: all - Default target. Cleans and compiles.
all: compile

# target: help - Display all targets.
help:
	egrep "#\starget:" [Mm]akefile  | cut -d " " -f3-

# target: clean - Deletes all generated files.
clean:
	$(RM) $(NAME).aux
	$(RM) $(NAME).bbl
	$(RM) $(NAME).blg
	$(RM) $(NAME).dvi
	$(RM) $(NAME).log
	$(RM) $(NAME).out
	$(RM) $(NAME).pdf
	$(RM) $(NAME).ps
	$(RM) $(NAME).tpt
	$(RM) $(NAME).toc
	$(RM) q.log

# target: compile - Compiles and renders documents.
compile:
	latex		$(NAME).tex	# Make aux that bibtex need.
	bibtex		$(NAME).aux	# Go through all references.
	latex		$(NAME).tex	# Make dvi.
	latex		$(NAME).tex	# Complete references.
	dvips		$(NAME).dvi	# Generate postscript.
	$(RM)		$(NAME).aux	# Delete because the current aux was not generate with hyperref and pdflatex will complain.
	pdflatex 	$(NAME).tex	# Generate PDF.
	pdflatex 	$(NAME).tex	# Complete references.
	thumbpdf 	$(NAME).pdf	# Make thumbnails for the PDF.
	pdflatex	$(NAME).tex	# Incorporate the thumbnails.

# target: wc - Count real words in the tex file.
wc:
	bin/texcount	$(NAME).tex

# target: cc - Count real characters in the tex file.
cc:
	bin/untex/untex	$(NAME).tex | wc -c
