require 'rubygems'
require 'set'
require 'csv'
require 'active_support/core_ext'
require 'gnuplot'
require 'pry'

require './yavf_result'

puts "Parsing CSV"
results = []
CSV.read("spring_vf.csv").tap do |csv|
  csv[1..-1].each do |row|
    results << YavfResult.new.tap do |result|
      row.each.with_index do |col, index|
        col_name = csv.first[index].parameterize("_")
        result[col_name] = col
      end
    end
  end
end

smart_results = []
CSV.read("smart_spring_vf.csv").tap do |csv|
  csv[1..-1].each do |row|
    smart_results << YavfResult.new.tap do |result|
      row.each.with_index do |col, index|
        col_name = csv.first[index].parameterize("_")
        result[col_name] = col
      end
    end
  end
end


puts "======"

processed_results = {}
results.each do |result|
  processed_results[result.sensing_interval] ||= 0.0
  processed_results[result.sensing_interval] += 1.0 / 20 if result.connectivity && result.converged
end

processed_smart_results = {}
smart_results.each do |result|
  processed_smart_results[result.sensing_interval] ||= 0.0
  processed_smart_results[result.sensing_interval] += 1.0 / 20 if result.connectivity && result.converged
end

puts "Generating graph"

p processed_results.to_a

Gnuplot.open do |gp|
  Gnuplot::Plot.new(gp) do |plot|
    plot.title  "Simulation (Robots : 32)"
    plot.grid
    plot.xlabel "Sensing Interval"
    plot.ylabel "Converged"


    plot.terminal "postscript eps enhanced color"
    plot.output "smart.eps"

    plot.data << Gnuplot::DataSet.new(processed_results.to_a.transpose) do |ds|
      ds.with = "linespoints"
      ds.title = "spring vf"
    end
    plot.data << Gnuplot::DataSet.new(processed_smart_results.to_a.transpose) do |ds|
      ds.with = "linespoints"
      ds.title = "smart spring vf"
    end
  end
end
