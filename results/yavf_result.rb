Array.class_eval do
  def sum; inject(&:+).to_f end
  def mean; sum / size end
  def filter_by(value)
    select do |element|
      yield(element) == value
    end
  end
end

class YavfResult
  COLUMNS = [:seed, :robot_count, :spring_constant, :damping_coefficient, :iteration, :moved_distance, :connectivity, :converged, :sensing_interval]
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

  def connectivity=(value)
    @connectivity = value == "TRUE" || value == "true"
  end

  def converged=(value)
    @converged = value == "TRUE" || value == "true"
  end

  def sensing_interval=(value)
    @sensing_interval = value.to_f
  end

  def similar?(result)
    robot_count == result.robot_count &&
     spring_constant == result.spring_constant &&
     damping_coefficient == result.damping_coefficient &&
     sensing_interval == result.sensing_interval
  end

  def to_a
    COLUMNS.map { |column| send(column) }
  end
end
