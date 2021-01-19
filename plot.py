import matplotlib.pyplot as plt
import numpy as np
import statistics
import sys, getopt

MODE = "r"
SPLITTER = " "


def read_from_file(filename):
    times = []
    with open(filename, MODE) as file:
        lines = file.readlines()
        
        for l in lines:
            list = l.split(SPLITTER)
            list = [int(x) for x in list]
            n = list[0]
            avg_time = sum(list[1:]) / len(list[1:])
            times.append((n, avg_time))
    return times


def plot_scatter(values, output_file):
    numbers = [i[0] for i in values]
    times = [i[1] for i in values]
    a, b, c = np.polyfit(numbers, times, 2)
    print(a, b, c)
    plt.xlabel("Size of the problem")
    plt.ylabel("Time [milliseconds]")
    plt.plot(numbers, times, 'o')
    plt.plot(np.unique(numbers), np.poly1d(np.polyfit(numbers, times, 2))(np.unique(numbers)))
    plt.savefig(output_file)
    plt.show()


def main(argv):
    input_file = ''
    output_file = 'output.png'
    try:
        opts, args = getopt.getopt(argv,"hi:o:",["ifile=", "ofile="])
    except getopt.GetoptError:
        print 'usage: plot.py -i <inputfile> -o <outputfile>'
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print 'usage: plot.py -i <inputfile> -o <outputfile>'
            sys.exit()
        elif opt in ("-i", "--ifile"):
            input_file = arg
        elif opt in ("-o", "--ofile"):
            output_file = arg
    values = read_from_file(input_file)
    plot_scatter(values, output_file)


if __name__ == '__main__':
    main(sys.argv[1:])
