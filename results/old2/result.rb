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

NilClass.class_eval do
  def +(value)
    nil
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
  INTEGERS = [:seed, :robot_count, :iteration]
  FLOATS = [:spring_constant, :damping_coefficient, :moved_distance, :sum_consumed_energy, :max_consumed_energy, :iteration_interval]
  BOOLEANS = [:connectivity, :converged]
  COLUMNS = PARAMETERS + RESULTS
  COLUMNS.each(&method(:attr_reader))
  INTEGERS.each do |parameter|
    define_method("#{parameter}=") do |value|
      instance_variable_set("@#{parameter}", value.to_i)
    end
  end
  FLOATS.each do |parameter|
    define_method("#{parameter}=") do |value|
      instance_variable_set("@#{parameter}", value.to_f)
    end
  end
  BOOLEANS.each do |parameter|
    define_method("#{parameter}=") do |value|
      value = value == "TRUE" || value == "true" if value.is_a? String
      instance_variable_set("@#{parameter}", value)
    end
  end

  def [](key)
    send(key)
  end

  def []=(key, value)
    send("#{key}=", value)
  end

  def real_converged
    connectivity && converged
  end

  def sum_consumed_energy
    real_converged ? @sum_consumed_energy : 0
  end

  def converged_time
    real_converged ? iteration * iteration_interval : 0
  end

  def parameters(seed = false)
    parameters = PARAMETERS
    parameters -= [:seed] unless seed
    parameters.map(&method(:[]))
  end

  def to_a
    COLUMNS.map { |column| send(column) }
  end
end
