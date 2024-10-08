
# MNIST Identifier - Jesse Andersen

This program emulates the process of identifying a digit from the MNIST data set, without the use of Machine Learning. The project was inspired by [The Stilwell Brain](https://www.youtube.com/watch?v=rA5qnZUXcqo), which displays the simplicity of the program as a low-level neural network.

# The Process

The user draws a digit on the drawing window (taking up about 70% of the window). The digit should be reasonably proportionate, and distinguishable at a quick glance by human eyes. The drawing will be encapsulated by a 5x5 grid of equal squares. Upon pressing enter, the program simplifies the user drawing by summarizing the data within those cells. The simplified image is then analyzed to check for the presence of certain figures. Those figures are then combined logically to determine which digit must have been the one drawn. The estimated weights of all the figures and digits is printed to the console, as well as the expected digit (the digit with the highest weight).

# JavaDoc

All classes were written and documented by me. There are no written tests for any methods or classes, but I repeatedly tested the overall workflow of the application while I built it up from scratch. This was my first time working with Swing, so it's quite likely there were somewhat more efficient ways to do what I've done with it here.


## Screenshots

Each figure is drawn here in a 5x5 grid, representing the cells that are checked when evaluating the presence of that figure. The darker pixels represent the absolutely necessary cells for the figure to be present, and the light ones represent cells that boost the presence of the figure when active, but do not hinder it when absent. Each figure is assigned an arbitrary letter below it that corresponds with the usage of it in the code. Beneath all of the figure definitions are a few examples of how the figures combine to make digits.
![App Screenshot](https://imgur.com/a/qhJr7Et)


## Full Demo
Attached is a full demonstration of the program detecting each digit from 0-9 correctly. Note that it allows various forms for certain numbers, as there are often multiple correct ways to draw a number.
https://drive.google.com/file/d/1aVinAmPI-c5pMsB4Tb-hO5agCdxUSlGV/view?usp=sharing


## Lessons Learned/Pitfalls of the Program

The main takeaway here is that manually adjusting the weights system of a neural network becomes increasingly more difficult and tedious as the complexity/size of the program increases. In the VisionPanel.java file, you can find my weighting calculations for each digit between lines 54 and 239. Each method in the LayerOne.java file below line 46 is the manually calculated weighting for each individual figure. This was exceptionally repetitive to create and adjust, and it often took hours of readjusting the weights to get the system working. At this point, it has about 80-90% accuracy when a digit is drawn to fair proportion and takes up at least 60% of the drawing canvas.

In the future, I would be interested in implementing this algorithm using an actual Machine Learning process. By storing a matrix for each digit representing the likelihood of each pixel's presence, we can compare a drawn image to each matrix and find the one with the least difference, that would be the prediction. If the prediction is incorrect, we can adjust the weightings of the predicted matrix and the correct matrix until this mistake is no longer made. This process can be repeated endlessly until the improvement of the algorithm is negligble.


## Authors

- [@Jemsbley](https://www.github.com/Jemsbley)

