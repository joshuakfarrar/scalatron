package org.presidentialinnovation.joshuakfarrar.scalatron

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PerceptronSpec extends AnyFlatSpec with Matchers {

  "A Perceptron" should "correctly calculate the activation function" in {
    val perceptron = Perceptron(Vector(0.2, 0.5), 0.05)
    val features = Vector(1.0, 2.0)
    perceptron.activation(features) should be (1.2)
  }

  it should "correctly predict Heads or Tails" in {
    val perceptron = Perceptron(Vector(0.2, 0.5), 0.05)
    perceptron.step(Vector(1.0, 2.0)) should be (Heads)
    perceptron.step(Vector(-1.0, -2.0)) should be (Tails)
  }

  it should "correctly calculate the error" in {
    val perceptron = Perceptron(Vector(0.2, 0.5), 0.05)
    perceptron.error(Heads, Tails) should be (1)
    perceptron.error(Tails, Heads) should be (-1)
    perceptron.error(Heads, Heads) should be (0)
    perceptron.error(Tails, Tails) should be (0)
  }

  it should "correctly update weights when training" in {
    val learningRate = 0.05
    val perceptron = Perceptron(Vector(0.2, 0.5), learningRate)
    val features = Vector(1.0, 2.0)

    // Manually calculate expected new weights
    val prediction = perceptron.step(features)
    val error = perceptron.error(Tails, prediction) // Let's assume the target is Tails
    val expectedWeights = (perceptron.weights zip features).map { case (w, f) => w + learningRate * error * f }

    // Train perceptron and get new weights
    val newPerceptron = perceptron.train(features, Tails) // Train with target Tails
    val newWeights = newPerceptron.weights

    // Check if the new weights match the expected weights
    newWeights should be (expectedWeights)
  }

  it should "learn to predict correctly after sufficient training" in {
    var perceptron = Perceptron(Vector(0.2, 0.5), 0.05)
    val features = Vector(1.0, 2.0)

    for(_ <- 1 to 1000) {
      perceptron = perceptron.train(features, Heads)
    }

    perceptron.step(features) should be (Heads)
  }

  it should "have a 95% confidence interval for accuracy above a threshold" in {
    // Random number generator
    val rand = new scala.util.Random

    // Generate complex data
    val totalData = List.fill(2000)(Vector(rand.nextDouble() * 2 - 1, rand.nextDouble() * 2 - 1))  // Random values between -1 and 1
    val totalTargets = totalData.map(v => if (v.sum > 0) Heads else Tails)  // Target is determined by the sum of the feature vector

    // Split into training and testing data
    val (trainData, testData) = totalData.splitAt(1000)
    val (trainTargets, testTargets) = totalTargets.splitAt(1000)

    // Train the perceptron with the training data
    var perceptron = Perceptron(Vector(0.2, 0.5), 0.05)
    for ((features, target) <- trainData zip trainTargets) {
      perceptron = perceptron.train(features, target)
    }

    // Now, let's purposely mess up the perceptron to demonstrate that the test works
    // Comment this line out to show that the Perceptron performs above the threshold
    // perceptron = Perceptron(Vector(0.2, 0.5), 0.05)

    // Compute the predictions on the testing data
    val predictions = testData.map(perceptron.step)

    // Calculate the proportion of correct predictions
    val correctPredictions = (predictions zip testTargets).count { case (p, t) => p == t }
    val proportionCorrect = correctPredictions.toDouble / testData.size

    // Compute the 95% confidence interval for the proportion
    val z = 1.96  // z-score for 95% confidence interval
    val interval = z * math.sqrt((proportionCorrect * (1 - proportionCorrect)) / testData.size)

    val lowerBound = proportionCorrect - interval
    val upperBound = proportionCorrect + interval

    // The lower bound of the confidence interval should be above the performance threshold
    val performanceThreshold = 0.85  // Replace with actual threshold
    lowerBound should be >= performanceThreshold
  }
}
