require 'rubygems'
require 'gnuplot'

require './result'

%w(converged_time converged sum_consumed_energy).each.with_index do |y|
  Gnuplot.open do |gp|
    Gnuplot::Plot.new(gp) do |plot|
      plot.title  "Simulation (Robots : 32)"
      plot.grid
      plot.xlabel "Sensing Interval"
      plot.xrange "[:2.5]" if y == "converged"
      plot.ylabel y.humanize
      plot.yrange "[:600]" if y == "converged_time"
      plot.yrange "[7000:8000]" if y == "sum_consumed_energy"

      plot.terminal "postscript eps enhanced color"
      plot.output "iteration_interval_#{y}.eps"

      ARGV.each do |csv|
        results = Results.parse_csv(csv)
        results.select!(&:real_converged) if y != "converged"
        results = results.average.sort_by(&:iteration_interval)
        spring_constant = results.first.spring_constant
        damping_coefficient = results.first.damping_coefficient

        plot.data << Gnuplot::DataSet.new(results.map { |result| [result.iteration_interval, result[y]] }.to_a.transpose) do |ds|
          ds.with = "linespoints"
          ds.title = "#{y.humanize} (#{spring_constant})"
          ds.smooth = "sbezier"
        end
      end
    end
  end
end
