require 'rubygems'
# require 'iconv'
# require 'roo'
require 'set'
require 'csv'
require 'active_support/core_ext'
require 'gnuplot'
require 'pry'

require './yavf_result'

results = []
robot_counts = Set.new
puts "Parsing CSV"
CSV.read(ARGV.first).tap do |csv|
  csv[1..-1].each do |row|
    YavfResult.new.tap do |result|
      row.each.with_index do |col, index|
        col_name = csv.first[index].parameterize("_")
        result[col_name] = col
      end
    end.tap do |result|
      if (result.connectivity && result.converged) || true
        results << result
        robot_counts << result.robot_count
      end
    end
  end
end

if ARGV.second == "average"
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
elsif ARGV.second == "smart"
  puts "Generating graph"
  p results.each
  # Gnuplot.open do |gp|
  #   Gnuplot::Plot.new(gp) do |plot|
  #     plot.title  "Simulation (Robots : #{robot_count})"
  #     plot.grid
  #     plot.xlabel "Damping coefficient"
  #     plot.ylabel "Iteration Count"
  #     plot.y2tics
  #     plot.y2label "Moved Distance"

  #     plot.terminal "postscript eps enhanced color"
  #     plot.output "smart.eps"

  #     %w(iteration moved_distance).each do |y|
  #       plot.data << Gnuplot::DataSet.new(results.filter_by(robot_count, &:robot_count).map { |result| [result.damping_coefficient, result[y]]}.transpose) do |ds|
  #         ds.with = "linespoints"
  #         ds.title = "#{y.humanize}"
  #         ds.title += " Count" if y == "iteration"
  #         ds.axes = "x1y2" if y == "moved_distance"
  #       end
  #     end
  #   end
  # end
else
  puts "Generating graph"
  spring_constant = results.first.spring_constant
  robot_counts = [32, 64]
  robot_counts.each do |robot_count|
    Gnuplot.open do |gp|
      Gnuplot::Plot.new(gp) do |plot|
        plot.title  "Simulation (Robots : #{robot_count})"
        plot.grid
        plot.xlabel "Damping coefficient"
        plot.ylabel "Iteration Count"
        plot.y2tics
        plot.y2label "Moved Distance"

        plot.terminal "postscript eps enhanced color"
        plot.output "spring_constant_#{spring_constant}_#{robot_count}.eps"

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
  end
  # %w(iteration moved_distance).each do |y|
  #   Gnuplot.open do |gp|
  #     Gnuplot::Plot.new(gp) do |plot|
  #       plot.title  "Simulation (spring constant : #{spring_constant})"
  #       plot.grid
  #       plot.xlabel "Damping coefficient"
  #       plot.ylabel y.titleize
  #       plot.y2ticts
  #       plot.y2label "Moved Distance"

  #       # plot.yrange "[0:300]"
  #       # plot.xtics 0.1

  #       plot.terminal "postscript eps enhanced color"
  #       plot.output "#{y}_spring_constant_#{spring_constant}.eps"

  #       robot_counts.each do |robot_count|
  #         plot.data << Gnuplot::DataSet.new(results.filter_by(robot_count, &:robot_count).map { |result| [result.damping_coefficient, result[y]]}.transpose) do |ds|
  #           ds.with = "linespoints"
  #           ds.title = "Robots: #{robot_count}"
  #         end
  #       end
  #     end
  #   end
  # end
end
