package org.presidentialinnovation.joshuakfarrar.scalatron

import cats.effect.{ExitCode, IO, IOApp}
import scala.util.Random

object App extends IOApp {

  def run(args: List[String]): IO[ExitCode] = for {
    // Generate some coin flips
    coinFlips <- IO {
      Seq.fill(25) {
        if (Random.nextDouble() < 0.5) Heads else Tails  // fair coin
      }
    }

    // Instantiate a new Perceptron
    initialPerceptron = Perceptron(Vector.fill(1)(0.8))

    // Train the perceptron on the coin flips
    finalPerceptron <- IO {
      coinFlips.sliding(2).foldLeft(initialPerceptron) { (perc, pair) =>
        val features = Vector(pair.head match {
          case Heads => 1.0
          case Tails => 0.0
        })
        val target = pair.last
        perc.train(features, target)
      }
    }

    // Try to predict the next coin flip
    lastFlip = Vector(coinFlips.last match {
      case Heads => 1.0
      case Tails => 0.0
    })
    prediction = finalPerceptron.step(lastFlip)

    // Print the prediction
    _ <- IO(println(s"The predicted next coin flip is: $prediction"))

  } yield ExitCode.Success
}
