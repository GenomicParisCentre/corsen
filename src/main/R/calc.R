

medianDistances <-function(dat, name='', nclass=50, plot=FALSE, summary=FALSE) {

  # ajoute n fois la distance dans le vecteur pour une particule d'intensité n
  r <- rep(dat[,2],dat[,1])

  if (summary==TRUE) {

    print(summary(r))
  }

  if (plot==T) {

    # Crée une nouvelle fenetre graphique
    x11()

    # Crée 4 zones dans le graphique
    par(mfrow = c(2,2))

    legend <- paste("Histogram of distances (",name,")")
    plot.new()
    text(x=10,y=10,label=legend)

    # Crée un histogramme de répartition des distances

    h <- hist(r, nclass=nclass, main = legend, xlab = "distances" )

    # Dessine le boxplot

    boxplot(r, main="Distribution of distances")

   # Calcule la lowess et la dessine
    l <- lowess(h$mids, h$counts)
    plot(l, main="Distribution after lowess",xlab = "distances"  )

    # Determine le point culminant de la lowess
    mat <- cbind(l$x,l$y)
    d <- subset(mat, mat[,2] == max(mat[,2]))[,1]
    #lines (c(d,d),c(0,l$y))
    abline(v=d)


  }

  m <- median(r)
  m
}

cellsDistances <- function(plot=FALSE, summary=FALSE, nclass=50) {

  # Get all data file in current directory
  files <- dir(pattern=".*\.data$")

  r <- NULL

  # Pour tous les fichiers
  for (f in files) {

    # Lit le fichier
    fdata <- as.matrix(read.table(f ,header=T))

    if (summary==TRUE) {

       print(f)
    }

    # Calcul la mediane des distances pour le fichier
    rd <- medianDistances(fdata, name=f, nclass=nclass, plot=plot, summary=summary)

    # Ajoute le résultat au vecteur de resultats
    r <- c(r, rd)
  }

  # retourne le résultat
  r
}


poolDistances <- function(plot=F, summary=FALSE, nclass=50) {

  # Get all data file in current directory
  files <- dir(pattern=".*\.data$")

  mat <- NULL

  # Pour tous les fichiers
  for (f in files) {

     # Lit le fichier
    fdata <- as.matrix(read.table(f ,header=T))

    # Ajoute à la suite de la matrice les données lues
    mat <- rbind(mat, fdata)
  }

  # Calcul la mediane des distances pour la matrice
  r <- medianDistances(mat, name="pool", nclass=nclass, plot=plot, summary=summary)

  # retourne le résultat
  r
}

