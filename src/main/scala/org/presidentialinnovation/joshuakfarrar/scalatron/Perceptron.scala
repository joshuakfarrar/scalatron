package org.presidentialinnovation.joshuakfarrar.scalatron

case class Perceptron(weights: Vector[Double], η: Double = 0.05) {

  def step(features: Vector[Double]): Coin = if (activation(features) > 0) Heads else Tails

  def activation(features: Vector[Double]): Double = (features zip weights).map { case (f, w) => f * w }.sum

  def error(target: Coin, prediction: Coin) = (target, prediction) match {
    case (Heads, Tails) => 1
    case (Tails, Heads) => -1
    case _ => 0
  }

  def train(features: Vector[Double], target: Coin): Perceptron = {
    val prediction = step(features)
    val err = error(target, prediction)
    val newWeights = (weights zip features).map { case (w, f) => w + η * err * f }
    copy(weights = newWeights)
  }
}