require 'rubygems'
require 'gnuplot'

require './result'

puts "Parsing CSV"
results = Results.parse_csv(ARGV.first)
results.select! do |result|
  result.connectivity && result.converged
end
robot_count = results.first.robot_count
spring_constant = results.first.spring_constant

puts "Calculating average of every seeds"
results = results.average.sort_by(&:damping_coefficient)

puts "Generating graph"
Gnuplot.open do |gp|
  Gnuplot::Plot.new(gp) do |plot|
    plot.title  "Simulation (Robots : #{robot_count})"
    plot.grid
    plot.xlabel "Damping coefficient"
    # plot.xtics 0.5
    plot.ylabel "Iteration Count"
    plot.y2tics
    plot.y2label "Moved Distance"

    plot.terminal "postscript eps enhanced color"
    plot.output "iteration_count_moved_distance_#{spring_constant}_#{robot_count}.eps"

    %w(iteration moved_distance).each do |y|
      plot.data << Gnuplot::DataSet.new(results.filter_by(robot_count, &:robot_count).map { |result| [result.damping_coefficient, result[y]]}.transpose) do |ds|
        ds.with = "linespoints"
        ds.title = "#{y.humanize}"
        ds.title += " Count" if y == "iteration"
        ds.axes = "x1y2" if y == "moved_distance"
      end
    end
  end
end

Gnuplot.open do |gp|
  Gnuplot::Plot.new(gp) do |plot|
    plot.title  "Simulation (Robots : #{robot_count})"
    plot.grid
    plot.xlabel "Damping coefficient"
    # plot.xtics 0.5
    plot.ylabel "Sum Consumed Energy"
    plot.y2tics
    plot.y2label "Max Consumed Energy"

    plot.terminal "postscript eps enhanced color"
    plot.output "consumed_energy_#{spring_constant}_#{robot_count}.eps"

    %w(sum_consumed_energy max_consumed_energy).each.with_index do |y, i|
      plot.data << Gnuplot::DataSet.new(results.filter_by(robot_count, &:robot_count).map { |result| [result.damping_coefficient, result[y]]}.transpose) do |ds|
        ds.with = "linespoints"
        ds.title = "#{y.humanize}"
        ds.axes = "x1y2" if i == 1
      end
    end
  end
end
