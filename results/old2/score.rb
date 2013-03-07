require 'rubygems'
require 'gnuplot'

require './result'

Gnuplot.open do |gp|
  Gnuplot::Plot.new(gp) do |plot|
    plot.title  "Simulation"
    plot.grid
    plot.xlabel "Damping coefficient"
    plot.ylabel "Sum Consumed Energy"

    plot.terminal "postscript eps enhanced color"
    plot.output "sum_consumed_energy.eps"

    ARGV.each do |csv|
      puts "Parsing #{csv}"
      results = Results.parse_csv(csv)
      results.select! do |result|
        result.connectivity && result.converged
      end
      results = results.average.sort_by(&:damping_coefficient)
      spring_constant = results.first.spring_constant

      plot.data << Gnuplot::DataSet.new(results.map { |result| [result.damping_coefficient, result.sum_consumed_energy]}.transpose) do |ds|
        ds.with = "lines"
        ds.smooth = "sbezier"
        ds.title = spring_constant
      end
    end
  end
end
