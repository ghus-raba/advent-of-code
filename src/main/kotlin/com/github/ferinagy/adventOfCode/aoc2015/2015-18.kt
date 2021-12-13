package com.github.ferinagy.adventOfCode.aoc2015

import com.github.ferinagy.adventOfCode.Coord2D
import com.github.ferinagy.adventOfCode.filterIn

fun main(args: Array<String>) {
    println("Part1:")
    println(part1(testInput1, 4))
    println(part1(input, 100))

    println()
    println("Part2:")
    println(part2(testInput1, 5))
    println(part2(input, 100))
}

private fun part1(input: String, steps: Int): Int {
    var field = input.lines().map { line -> line.map { it == '#' } }
    repeat(steps) {
        field = field.update()
    }

    return field.sumOf { row -> row.count { it } }
}

private fun part2(input: String, steps: Int): Int {
    var field = input.lines().map { line -> line.map { it == '#' } }.stuckCorners()
    repeat(steps) {
        field = field.update().stuckCorners()
    }

    return field.sumOf { row -> row.count { it } }
}

private fun List<List<Boolean>>.update() = mapIndexed { row, booleans ->
    booleans.mapIndexed { col, b ->
        val adjacent = Coord2D(col, row).adjacent(true)
            .filterIn(first().indices, indices)
            .map { this[it.y][it.x] }
            .count { it }

        b && adjacent in 2..3 || !b && adjacent == 3
    }
}

private fun List<List<Boolean>>.stuckCorners() = mapIndexed { row, booleans ->
    if (row == 0 || row == lastIndex) {
        booleans.mapIndexed { col, b ->
            if (col == 0 || col == booleans.lastIndex) {
                true
            } else {
                b
            }
        }
    } else booleans
}

private const val testInput1 = """.#.#.#
...##.
#....#
..#...
#.#..#
####.."""

private const val input =
    """####.#.##.###.#.#.##.#..###.#..#.#.#..##....#.###...##..###.##.#.#.#.##...##..#..#....#.#.##..#...##
.##...##.##.######.#.#.##...#.#.#.#.#...#.##.#..#.#.####...#....#....###.#.#.#####....#.#.##.#.#.##.
###.##..#..#####.......#.########...#.####.###....###.###...#...####.######.#..#####.#.###....####..
....#..#..#....###.##.#.....##...#.###.#.#.#..#.#..##...#....#.##.###.#...######......#..#.#..####.#
..###.####..#.#.#..##.#.#....#......#.##.##..##.#.....##.###.#..###...###.#.##..#.#..###....####.#.#
#.#...#......####.#..##.####.#.#.#...##..###.##.#...#..#..###....#.#....#..##..#....##.....##.#...#.
....##.#.#.#.##..##...##..##..#....#....###...####.###...##.#...#..#....##.....#..#.#####.###.###.##
#...##..#.#..#....#..########.##....##..##.###..#.#..#..#.##.##.#..##..######....####..#####.#.###..
.####...######.#..#.##.#.#..####...####.##.#.#......#...##....##..#...###..#.####......###......#.##
.####.###..#..#####.##...###......#...###..#..##..#.#....##.##.#.##.###..#..#..###.#..#.#....####.##
#..#..##.##.##.###.#.##.##.#.#.#....#....#.####.#.##...#####...###.#####.#.#.#....####..###..###..##
#.##....#...########..##...#.#.##.......#.#..##...####...#.####.####..##...##.#....###.#.####...#.##
#.#...##..#.##.##..##....#.....##.##.....#...###...#..#...####.##.####..#...##..##.##.##.##..##...##
.#..###...#.#.....#######..##.###....##..#.##.#......###.##....#......###...#.##....#.....##......##
..##....#.###...###..####.##..#..##.##......##.#.....#...#..#..##...###..#.####...#...#..##.#..##..#
...#.#.#...#.#..#.##....##..#...#.##..#......#.#.....#####.##.#...#######.#.#..#.####..###.....###.#
.#....#.#.##..####.#####..#.#######..#.##.###...##.##....##..###..#.##.###.......#....#..######.####
#..#.##.##..#..#..##.####.#.#.#.#..#.##...#..######....#.##.#..##.##.######.###.###.###...#.....#.#.
.#.......#...#.####.##...#####..##..#.#....##..#.#.#.####.#.##....#..##.##..#.###.....#.##.##.#.##.#
#..##..##...#....#.##.#...#.#....#......####...##..#...##.##.#..#########..#..#.##.##..#.#.#######..
#.......#####..###..######.#..##.#.#####..##...###...#.####.##...###..#.#.#####....#...#.##...#.#..#
.##..#...#####.##.##......#...#.#.#.###.#.#.#...##.#..#....###.....#..#.#.###......#####.###.#..##.#
.....###.#.#.#..##...#...###..#...#.#.##..###.##.#####.##..#.#.#.#.#####....#.#.#####...##.#..#.#.#.
###...##.#..#.####..##.#..##.#.#.#...#.#..#..##..##..#.#.#.#.##...##..#..#.....#....#####.#.#.####.#
....##....#.#.....#...###.#...##..##.##..#..###..##.###..#####..#...#####.##.#..#.#.#.###...####.###
##.##.##.#...#..#...........##.##.###.#...###.####.#..#..#...#..#..####.#.###########..#.###.###.#.#
##.##..##.####..###...##...#....###.###.#..##..#..#.###.#..####.#..##.#.#...#..#.#.##.##...#...#....
..##...#.#.##....##...#.#.#......##.##.#.#.####.####....####.#.###.##.#.#..####..#..######..#..#.#..
####.#.##.......##.###....##.#..####.#.#######..#...###..##.##..#...#...####........#.#..##...#....#
#..#.#.....#..#.###..#.#...###..##...#.#..#.#.##..#...##.##.##.#.#.#..#.####.########....########..#
#...#..##.##..#.#.#.##.##.##.#..#..#.##....#....###.#.###.#.#..#....#...##..#.....####...##.#..#...#
.###...##...####....###.##.#..####...##.#.##.#..##..##....#....##.#...#..#..##..##..##.#...#...###..
.#..##.#..##..####..#.#.##..###.#...#....##.###...#.###....#.#.#........#..#.#.#..##..#####..#..#.#.
.#.##.....#..#...#.##.....#.##..#..#....#..#..#....#.##..##...#.##.##..##..#.#.#.##..####.##..#.#..#
...###.#.....#...#.##.#.###.#...##..#.###..#..#..#.#..#...###.#.##.##.##.#.##.#####.#..#.#..#.#...##
#.#.#.#.##.#.....##..#.###......##.#.##..#...#.########.##.###..#..#..##..##.#..##..###.#.###...#.#.
..##...##...#...###.#..##..#..#..#.#.##..##......##..##.....##.....####..#.##......#..####...###..##
##.......#..##....###...###......#.##.##....######..###.##...##.#...#...#.....#.###.#.#..#.##..#..#.
#.#..#..#.#####.##.##.###..#...###.....#..##..####...#.#.###....#..#.#.###.####..#.#........##.#....
..###.#...##.#.####.#.##.##.....##...#.##.#.###.#.#..##.#..##..#..##.##....#.#####.##..#######.....#
###.###..##.#..##...#####..##.####....#.##......##......#.#....##.####.#.#.#.###...#..####..#.######
#..###...#.#.......#..####.####...#....###.###...#.##..##..#..##.##.......####.##...#.#.#.##.#.#..#.
..#...#..###.##..#.#.#.##..#..#.#.......###..###..#####.#.#.#.#.#..#.#.#.#..###....#.####..###...#..
...######.###....#..####.####......#...#.###.#....#...####.##........##...##.#..##.###.#..#..##..###
.#..###.####.###.#.#..#..#..#.##.#.#.###.##..####.#####..##....##.#.##...###.####.#.#######.#..#..#.
.#..##.#..##..#...##...#..#..##.#.#....##.##...###.#.#...##..##..#.###.#.#.#.#...#....#.#..#.#.###.#
.###..#.#..####.#########...####....####.#.##...##.##..#.##.#........#.....###.###.######.##.....###
..##.##..##..#.####.#..#####.#....##.##.#####.....#.#......##...#####..####....###..#.#...#..####..#
.#..##..##.##.##.##.#.###.###.#..#..#...###.#.##..##...##...###...##.###..#.#.#####.#.#.##....#.##..
...#.#....##.#.....###.##...#..##....#...###....#..#.###...##.#...###.#....#...##..###.#.....##....#
.#######..#...##.#.###.##.#.###...##......#.###.#...#.###.#.#.#..#..#####..#########...##..##...#..#
.#..#.##...#.#..#.##..#.#.#.##.....####.#..#.###..##.#.#.#...#....#.#..##.######...#.#..##.##...#..#
#.#######.#####..#####.##.##.#.#.##.###..#....####.#..##.##.######..###...#.#..#.####.##.##....####.
...##..#...##..#..#.....#.##...#.....##.#####.###.########.######..#...###..#.##.#.#.##..#.#.##..##.
#..#..#.#....###.#...##..####.#.##..#.####.###..##.#...#.###.#..#.##..#######.#...#..#.#..##.#....##
..#.##.#.####..##.###.###..#.##.#.####..##....##.###.#..##.#.###.###.##.##.#####..#.#...########....
.#.#.###..###...#...#..##.##......#..#...#.#.#.######.#.#...##..##........#....###..##...#..##.##...
##..#....##.###...##.#.##.##.##..#....#.#.#..#..####.##..#...#...#..#..#####.###...#..###..#...#.#..
##.#.#.##.###.....######.#.....#...#.##....###.#.##.#.#.##..##.######.#####....#.#####...##.#..###.#
######.#...####..###..##..#..##...#.#....##.#...##...#.....#...##....#.##..###..###...###..#..######
.....##.........#####.#.##..#..#.#.#.#.##...#....#.....###.########...#..####..#...#...##..#.##.##.#
#..###...#.##.##.#.#..####.#.....##..###....##..#...#.#...##.##..###..####...#.####..##..#..##..#...
#.####.#..##.#..#.....#..#.#..###...######.#.........####....###..#.#.#.##.#..#...#..####.....##..#.
..##....#.###.......##.#...#.####..##....##.#..#....#######...####.##..#####.#.#.#.#.##..##..#.#.#..
#.#.#.###..#..#.#..#.#.###....#...#####.###...........#.#....#####...#..####....#...###.#..#..####..
.......#.####.##...#..#.##..###..#..#.#.#.#.###....#....#.#.#..#.#..##.#####.#.....#.##.#.###.###.##
..###...#..#...####.#..##..##.#.#..#...#.#..#....###.#..####..######...####.#.##..#.#..###...##.####
..#.###..#.#...##...#.#....#..#...#.#..##.######.######.#.##.....#..##.#..###..#..#.##.###...#..#.##
####..##.####.....#...#.#.###..#...####.###.#.#.#.......##...#....#..#....#.#......###...#####.#.##.
#..##..#..#.####...#####.#.###.##.#.##.....#.#..#.##........######.#.#.###....##.##..##..########.##
#.#....###.##....#######.#...#.#.#.#..##.#.##...#.###...#.#.#..#.#..####.#.#..#..#.##.####....#..##.
####.##....#.......###..#..##.#.#.##..#...#...##.###....##..###.#.#...#..#.....##.###.##...###....##
..##.#..#....######..#.##.#.#...##..####.#####...##.#..###.##...#..####..###.##..##.##.#####.#..#.#.
.#.##..#..##.#.###.###....#.#..#....#...###.##.#.#.####.....#....#...#.....#....#.#.###.#..#.##..###
..###.#.#.##...##.##.##.#...#####.#..##.#....##..####...###..#....#.##...#........#####.#.###.#..#..
....#..##..##....#.#....#.#..##...##.#...##.###.#.#..###..##.##.##..#.#.#..#.#.##.......#.##.###..#.
.#..##.##.####.##....##.##.....###..##.#.##...#..###....###.###....#.#....#....#.##.#.##.#.##.....##
#.#..#.##.###.#.######.....###.#..#...#.#.....##.###.#...#.#..###.#.....##.###.#.###.####..#####.#..
#.#.##......#.##.#.#..##....#..###.#.###...##...###.#..#.##...#..#.##..##.#...######.##.....#####.##
#.#..#####....###.###...#.......#....###.##...#..#.##..#...#####..#..#.##......###...#...###..#.#..#
#.##..##.##.#..#.##.##..#.###.##.........###.#.#..#.#.....#.#...#.#.##.#.##.#...#...####.#.......##.
.#...####.##..#..##....####..######...#.#..##.##.....#####.#...#..#.####.#######...#.#####..#.###...
.#..######.#.##..##...##.....###.#..##..#...####..###...###.###..#..######.#....########..#####...#.
#..##.......#####...###..#.#.##.#..###.#...##.#..#.##.###...###...##.#..##..########..#.#..##..#.###
.#.#..#...#.#..#..##...#.#.##...###..#..#....###.#....#.##....###.###..##..#.#.####..####.#######.##
...##..##.##.###.##.###...##.#.#.....##.####..#..##.#..#.####...##..#..#.##...##...###.##.#.......##
.#.....#.##..#.#.....#.##.##..###..#....###...#.#....##########.##.###.#...#.####..####.#..#.#..###.
.##.#.#.##..#..###.###.##.#########.#.#.#.#.##.###..##..#.##.####......#####...#..####.#.##..#####.#
..#....###...##....#.###..##..#..####.##..####.#..####.###.#....####.....#.###..##...##..####...##.#
.###.....###.##.##..###.###.....##..#.######.#.#..##..#.##.#..#.#.#....#...#.#.#...#...##....#..##.#
..##....#..#####....#..####.#.#...##.#....##..##.###.###....###......#...#.#####.......#...#.....###
###.#..#.#.##..#..#...#.#....###.##.#.###.#...#.##.#..#.#.......#.#.#.###.####.###....#..##..#####..
.#..#######.#..###.#.##.#####.#####...##..#.####.#.#.##..###...#..##.##..#.#.###..#....#..#...###.#.
..#####..#.##.....###..##.#...#.#.#..#######.#..#...#.##.##.#.#....####...###..##...#....####.#..#.#
.####..#.#.##.###.#.##.....#..##.#.....###.....#..##...#....###.###..#......###.#.#.#.##.#.##..#...#
##.#..##.#..##...#.#....##..######..#.....#..#...#####....##......####.##..#...##..#.##.#.#######..#
##..####.#...##...#.#####.#.#..#....#.#..##.####.#..######.#..#..#.......#####..#..#..###.##...##.##
#.####......#.###...#..####.#..##.##..#.#...##.###.#...#####..####.#..#.#.....#.##...###...#.#....##
###.#.#.##.######......#.#.#.#.#........#..#..###.#.#.#..#.........#..#....#.#..#..#..###.##......##
##.#########...#...###..#.###.....#.#.##.........###....#.####.#...###.#..##..#.###..#..##......#.##"""
