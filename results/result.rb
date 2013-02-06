require 'rubygems'
require 'csv'
require 'active_support/core_ext'

TrueClass.class_eval do
  def to_i
    1
  end

  def to_f
    to_i.to_f
  end
end

FalseClass.class_eval do
  def to_i
    0
  end

  def to_f
    to_i.to_f
  end
end

Array.class_eval do
  def sum; inject(&:+).to_f end
  def mean; sum / size end
  def filter_by(value)
    select do |element|
      yield(element) == value
    end
  end
end

class Results < Array
  def self.parse_csv(csv)
    csv = CSV.read(csv)
    results = csv[1..-1].map do |row|
      Result.new.tap do |result|
        row.each.with_index do |col, index|
          col_name = csv.first[index].parameterize("_")
          result[col_name] = col
        end
      end
    end
    new results
  end

  def average
    group_by(&:parameters).values.map do |similar_results|
      similar_results.first.clone.tap do |result|
        result.iteration = similar_results.map(&:iteration).mean
        result.moved_distance = similar_results.map(&:moved_distance).mean
        result.sum_consumed_energy = similar_results.map(&:sum_consumed_energy).mean
        result.max_consumed_energy = similar_results.map(&:max_consumed_energy).mean
        result.converged = similar_results.map do |similar_result|
          similar_result.connectivity && similar_result.converged ? 1 : 0
        end.mean
      end
    end
  end
end

class Result
  PARAMETERS = [:seed, :robot_count, :spring_constant, :damping_coefficient, :iteration_interval]
  RESULTS = [:iteration, :moved_distance, :sum_consumed_energy, :max_consumed_energy, :connectivity, :converged]
  COLUMNS = PARAMETERS + RESULTS
  COLUMNS.each(&method(:attr_reader))

  def [](key)
    send(key)
  end

  def []=(key, value)
    send("#{key}=", value)
  end

  def seed=(value)
    @seed = value.to_i
  end

  def robot_count=(value)
    @robot_count = value.to_i
  end

  def spring_constant=(value)
    @spring_constant = value.to_f
  end

  def damping_coefficient=(value)
    @damping_coefficient = value.to_f
  end

  def iteration=(value)
    @iteration = value.to_i
  end

  def moved_distance=(value)
    @moved_distance = value.to_f
  end

  def sum_consumed_energy=(value)
    @sum_consumed_energy = value.to_f
  end

  def max_consumed_energy=(value)
    @max_consumed_energy = value.to_f
  end

  def connectivity=(value)
    @connectivity = value == "TRUE" || value == "true"
  end

  def converged=(value)
    @converged = if value.is_a? String
                   value == "TRUE" || value == "true"
                 else
                   value
                 end
  end

  def iteration_interval=(value)
    @iteration_interval = value.to_f
  end

  def score
    iteration + moved_distance
  end

  def parameters(seed = false)
    parameters = PARAMETERS
    parameters -= [:seed] unless seed
    parameters.map(&method(:[]))
  end

  def similar?(result)
    parameters == result.parameters
  end

  def to_a
    COLUMNS.map { |column| send(column) }
  end
end
