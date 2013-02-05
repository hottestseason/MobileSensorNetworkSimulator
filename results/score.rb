require 'rubygems'
require 'gnuplot'

require './result'

Gnuplot.open do |gp|
  Gnuplot::Plot.new(gp) do |plot|
    plot.title  "Simulation"
    plot.grid
    plot.xlabel "Damping coefficient"
    plot.xrange "[:30]"
    plot.yrange "[:2500]"

    plot.terminal "postscript eps enhanced color"
    plot.output "score_damping_coefficient.eps"

    ARGV.each do |csv|
      results = Results.parse_csv(csv)
      results.select! do |result|
        result.connectivity && result.converged
      end
      results = results.average.sort_by(&:damping_coefficient)
      plot.data << Gnuplot::DataSet.new(results.map { |result| [result.damping_coefficient, result.score]}.transpose) do |ds|
        ds.with = "lines"
        ds.smooth = "sbezier"
      end
    end
  end
end
