newFeatures.tex: newFeatures.md
	pandoc -f markdown -t latex -o newFeatures.tex newFeatures.md
define HEADER
\documentclass{article}
\\begin{document}
endef

define FOOTER
\end {document}
endef
export HEADER
export FOOTER
newFeatures.pdf: newFeatures.tex
	@echo "$$HEADER" | cat - newFeatures.tex  > temp.tex;
	@echo "$$FOOTER" >>temp.tex
	pdflatex temp.tex
	mv temp.pdf newFeatures.pdf
	rm temp.aux temp.log temp.tex

clean:
	rm -f newFeatures.pdf newFeatures.tex
