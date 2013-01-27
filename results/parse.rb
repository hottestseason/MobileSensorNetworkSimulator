require 'rubygems'
# require 'iconv'
# require 'roo'
require 'set'
require 'csv'
require 'active_support/core_ext'
require 'gnuplot'
require 'pry'

require './yavf_result'

Array.class_eval do
  def sum; inject(&:+).to_f end
  def mean; sum / size end
  def filter_by(value)
    select do |element|
      yield(element) == value
    end
  end
end

results = []
robot_counts = Set.new
puts "Parsing CSV"
CSV.parse(STDIN.read).tap do |csv|
  csv[1..-1].each do |row|
    YavfResult.new.tap do |result|
      row.each.with_index do |col, index|
        col_name = csv.first[index].parameterize("_")
        result[col_name] = col
      end
    end.tap do |result|
      if result.connectivity && result.converged #&& [20].include?(result.robot_count)
        results << result
        robot_counts << result.robot_count
      end
    end
  end
end

if ARGV.first == "average"
  puts "Calculating average of every seeds"
  spring_constant = results.first.spring_constant
  results = results.select do |result|
    result.spring_constant == spring_constant && result.seed == 0
  end.map do |result|
    results.select(&result.method(:similar?)).tap do |similar_results|
      result.iteration = similar_results.map(&:iteration).mean
      result.moved_distance = similar_results.map(&:moved_distance).mean
    end
    result
  end

  CSV.open(ARGV.last, "w") do |csv|
    csv << YavfResult::COLUMNS
    results.each do |result|
      csv << result.to_a
    end
  end
else
  puts "Generating graph"
  spring_constant = results.first.spring_constant
  %w(iteration moved_distance).each do |y|
    Gnuplot.open do |gp|
      Gnuplot::Plot.new(gp) do |plot|
        plot.title  "simluation (spring constant : #{spring_constant})"
        plot.xlabel "damping coefficient"
        # plot.xtics 0.1

        plot.terminal "postscript eps enhanced color"
        plot.output "#{y}_spring_constant_#{spring_constant}.eps"

        robot_counts.each do |robot_count|
          plot.data << Gnuplot::DataSet.new(results.filter_by(robot_count, &:robot_count).map { |result| [result.damping_coefficient, result[y]]}.transpose) do |ds|
            ds.with = "linespoints"
            ds.title = "#{y} (#{robot_count})"
          end
        end
      end
    end
  end
end
