require 'rubygems'
require 'gnuplot'

require './result'

%w(iteration moved_distance sum_consumed_energy).each do |y|
  Gnuplot.open do |gp|
    Gnuplot::Plot.new(gp) do |plot|
      plot.title  "Simulation"
      plot.grid
      plot.xlabel "Damping coefficient"
      plot.xrange "[:2.5]"
      plot.ylabel y.humanize

      plot.terminal "postscript eps enhanced color"
      plot.output "robots_#{y}.eps"

      ARGV.each do |csv|
        results = Results.parse_csv(csv)
        results.select!(&:real_converged)
        results = results.average.sort_by(&:damping_coefficient)
        robot_count = results.first.robot_count
        spring_constant = results.first.spring_constant

        plot.data << Gnuplot::DataSet.new(results.filter_by(robot_count, &:robot_count).map { |result| [result.damping_coefficient, result[y]]}.transpose) do |ds|
          ds.with = "linespoints"
          ds.smooth = "sbezier"
          ds.title = robot_count
        end
      end
    end
  end
end
