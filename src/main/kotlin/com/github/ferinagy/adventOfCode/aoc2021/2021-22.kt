package com.github.ferinagy.adventOfCode.aoc2021

import com.github.ferinagy.adventOfCode.Coord3D
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    println("Part1:")
    println(part1(testInput1))
    println(part1(testInput2))
    println(part1(input))

    println()
    println("Part2:")
    println(part2(testInput1))
    println(part2(testInput2))
    println(part2(input))
}

private fun part1(input: String): Int {
    val ops = input.lines().map {
        val (op, x1, x2, y1, y2, z1, z2) = regex.matchEntire(it)!!.destructured
        val cuboid = Cuboid(Coord3D(x1.toInt(), y1.toInt(), z1.toInt()), Coord3D(x2.toInt(), y2.toInt(), z2.toInt()))
        Operation(op == "on", cuboid)
    }

    val cubes = mutableMapOf<Coord3D, Boolean>().withDefault { false }
    ops.forEach {
        for (x in it.cuboid.start.x.coerceAtLeast(-50) .. it.cuboid.end.x.coerceAtMost(50)) {
            for (y in it.cuboid.start.y.coerceAtLeast(-50) .. it.cuboid.end.y.coerceAtMost(50)) {
                for (z in it.cuboid.start.z.coerceAtLeast(-50) .. it.cuboid.end.z.coerceAtMost(50)) {
                    val cube = Coord3D(x, y, z)
                    cubes[cube] = it.turnOn
                }
            }
        }
    }

    return cubes.count { it.value }
}

private fun part2(input: String): Long {
    val ops = input.lines().map {
        val (op, x1, x2, y1, y2, z1, z2) = regex.matchEntire(it)!!.destructured
        val cuboid = Cuboid(Coord3D(x1.toInt(), y1.toInt(), z1.toInt()), Coord3D(x2.toInt(), y2.toInt(), z2.toInt()))
        Operation(op == "on", cuboid)
    }

    val cuboidsOn = mutableSetOf<Cuboid>()
    val queue = ArrayDeque<Cuboid>()
    ops.forEach { op ->
        if (op.turnOn) {
            queue += op.cuboid
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                var didIntersect = false
                for (c in cuboidsOn) {
                    val intersect = current.intersect(c)
                    if (intersect != null) {
                        didIntersect = true
                        val (inter, first, second) = intersect
                        cuboidsOn.remove(c)
                        cuboidsOn += inter
                        cuboidsOn += second
                        queue += first

                        break
                    }
                }

                if (!didIntersect) cuboidsOn += current
            }
        } else {
            queue += op.cuboid
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                for (c in cuboidsOn) {
                    val intersect = current.intersect(c)
                    if (intersect != null) {
                        val (_, first, second) = intersect
                        cuboidsOn.remove(c)
                        cuboidsOn += second
                        queue += first
                        break
                    }
                }
            }
        }
    }

    return cuboidsOn.sumOf { it.size() }
}

private data class Cuboid(val start: Coord3D, val end: Coord3D)

private fun Cuboid.size(): Long = xRange.count().toLong() * yRange.count() * zRange.count()

private val Cuboid.xRange get() = start.x..end.x
private val Cuboid.yRange get() = start.y..end.y
private val Cuboid.zRange get() = start.z..end.z

private data class IntersectionResult(val intersect: Cuboid, val fromFirst: List<Cuboid>, val fromSecond: List<Cuboid>)

private fun Cuboid.intersect(other: Cuboid): IntersectionResult? {
    val c1 = Coord3D(max(start.x, other.start.x), max(start.y, other.start.y), max(start.z, other.start.z))
    val c2 = Coord3D(min(end.x, other.end.x), min(end.y, other.end.y), min(end.z, other.end.z))
    val intersect = Cuboid(c1, c2)
    if (intersect.size() == 0L) return null

    val first = this - intersect
    val second = other - intersect

    return IntersectionResult(intersect, first, second)
}

private operator fun Cuboid.minus(other: Cuboid): List<Cuboid> {
    return listOf(
        Cuboid(
            Coord3D(start.x, start.y, start.z),
            Coord3D(other.start.x - 1, end.y, end.z),
        ),
        Cuboid(
            Coord3D(other.start.x, start.y, start.z),
            Coord3D(other.end.x, end.y, other.start.z - 1),
        ),
        Cuboid(
            Coord3D(other.start.x, start.y, other.start.z),
            Coord3D(other.end.x, other.start.y - 1, other.end.z),
        ),
        Cuboid(
            Coord3D(other.start.x, other.end.y + 1, other.start.z),
            Coord3D(other.end.x, end.y, other.end.z),
        ),
        Cuboid(
            Coord3D(other.start.x, start.y, other.end.z + 1),
            Coord3D(other.end.x, end.y, end.z),
        ),
        Cuboid(
            Coord3D(other.end.x + 1, start.y, start.z),
            Coord3D(end.x, end.y, end.z),
        )
    ).filter { it.size() != 0L }
}

private data class Operation(val turnOn: Boolean, val cuboid: Cuboid)

private val regex = """(on|off) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""".toRegex()

private const val testInput1 = """on x=-20..26,y=-36..17,z=-47..7
on x=-20..33,y=-21..23,z=-26..28
on x=-22..28,y=-29..23,z=-38..16
on x=-46..7,y=-6..46,z=-50..-1
on x=-49..1,y=-3..46,z=-24..28
on x=2..47,y=-22..22,z=-23..27
on x=-27..23,y=-28..26,z=-21..29
on x=-39..5,y=-6..47,z=-3..44
on x=-30..21,y=-8..43,z=-13..34
on x=-22..26,y=-27..20,z=-29..19
off x=-48..-32,y=26..41,z=-47..-37
on x=-12..35,y=6..50,z=-50..-2
off x=-48..-32,y=-32..-16,z=-15..-5
on x=-18..26,y=-33..15,z=-7..46
off x=-40..-22,y=-38..-28,z=23..41
on x=-16..35,y=-41..10,z=-47..6
off x=-32..-23,y=11..30,z=-14..3
on x=-49..-5,y=-3..45,z=-29..18
off x=18..30,y=-20..-8,z=-3..13
on x=-41..9,y=-7..43,z=-33..15
on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
on x=967..23432,y=45373..81175,z=27513..53682"""

private const val testInput2 = """on x=-5..47,y=-31..22,z=-19..33
on x=-44..5,y=-27..21,z=-14..35
on x=-49..-1,y=-11..42,z=-10..38
on x=-20..34,y=-40..6,z=-44..1
off x=26..39,y=40..50,z=-2..11
on x=-41..5,y=-41..6,z=-36..8
off x=-43..-33,y=-45..-28,z=7..25
on x=-33..15,y=-32..19,z=-34..11
off x=35..47,y=-46..-34,z=-11..5
on x=-14..36,y=-6..44,z=-16..29
on x=-57795..-6158,y=29564..72030,z=20435..90618
on x=36731..105352,y=-21140..28532,z=16094..90401
on x=30999..107136,y=-53464..15513,z=8553..71215
on x=13528..83982,y=-99403..-27377,z=-24141..23996
on x=-72682..-12347,y=18159..111354,z=7391..80950
on x=-1060..80757,y=-65301..-20884,z=-103788..-16709
on x=-83015..-9461,y=-72160..-8347,z=-81239..-26856
on x=-52752..22273,y=-49450..9096,z=54442..119054
on x=-29982..40483,y=-108474..-28371,z=-24328..38471
on x=-4958..62750,y=40422..118853,z=-7672..65583
on x=55694..108686,y=-43367..46958,z=-26781..48729
on x=-98497..-18186,y=-63569..3412,z=1232..88485
on x=-726..56291,y=-62629..13224,z=18033..85226
on x=-110886..-34664,y=-81338..-8658,z=8914..63723
on x=-55829..24974,y=-16897..54165,z=-121762..-28058
on x=-65152..-11147,y=22489..91432,z=-58782..1780
on x=-120100..-32970,y=-46592..27473,z=-11695..61039
on x=-18631..37533,y=-124565..-50804,z=-35667..28308
on x=-57817..18248,y=49321..117703,z=5745..55881
on x=14781..98692,y=-1341..70827,z=15753..70151
on x=-34419..55919,y=-19626..40991,z=39015..114138
on x=-60785..11593,y=-56135..2999,z=-95368..-26915
on x=-32178..58085,y=17647..101866,z=-91405..-8878
on x=-53655..12091,y=50097..105568,z=-75335..-4862
on x=-111166..-40997,y=-71714..2688,z=5609..50954
on x=-16602..70118,y=-98693..-44401,z=5197..76897
on x=16383..101554,y=4615..83635,z=-44907..18747
off x=-95822..-15171,y=-19987..48940,z=10804..104439
on x=-89813..-14614,y=16069..88491,z=-3297..45228
on x=41075..99376,y=-20427..49978,z=-52012..13762
on x=-21330..50085,y=-17944..62733,z=-112280..-30197
on x=-16478..35915,y=36008..118594,z=-7885..47086
off x=-98156..-27851,y=-49952..43171,z=-99005..-8456
off x=2032..69770,y=-71013..4824,z=7471..94418
on x=43670..120875,y=-42068..12382,z=-24787..38892
off x=37514..111226,y=-45862..25743,z=-16714..54663
off x=25699..97951,y=-30668..59918,z=-15349..69697
off x=-44271..17935,y=-9516..60759,z=49131..112598
on x=-61695..-5813,y=40978..94975,z=8655..80240
off x=-101086..-9439,y=-7088..67543,z=33935..83858
off x=18020..114017,y=-48931..32606,z=21474..89843
off x=-77139..10506,y=-89994..-18797,z=-80..59318
off x=8476..79288,y=-75520..11602,z=-96624..-24783
on x=-47488..-1262,y=24338..100707,z=16292..72967
off x=-84341..13987,y=2429..92914,z=-90671..-1318
off x=-37810..49457,y=-71013..-7894,z=-105357..-13188
off x=-27365..46395,y=31009..98017,z=15428..76570
off x=-70369..-16548,y=22648..78696,z=-1892..86821
on x=-53470..21291,y=-120233..-33476,z=-44150..38147
off x=-93533..-4276,y=-16170..68771,z=-104985..-24507"""

private const val input = """on x=-45..1,y=-25..23,z=-14..35
on x=-30..18,y=-39..14,z=-1..48
on x=-40..10,y=-45..5,z=-47..7
on x=-25..19,y=-21..32,z=-10..35
on x=-16..35,y=-39..14,z=-5..39
on x=-36..13,y=-46..-2,z=-40..12
on x=-22..27,y=-29..20,z=-2..49
on x=-37..15,y=-31..18,z=-23..23
on x=-26..18,y=-12..36,z=-1..43
on x=-14..38,y=-39..12,z=-17..32
off x=-11..2,y=-40..-31,z=1..12
on x=-25..28,y=-2..49,z=-29..21
off x=8..23,y=-2..13,z=-27..-17
on x=-47..7,y=-16..35,z=-7..45
off x=39..49,y=-38..-21,z=-7..5
on x=-44..1,y=-5..43,z=-21..25
off x=5..22,y=-31..-22,z=8..17
on x=-6..47,y=-13..33,z=-36..12
off x=-6..13,y=19..31,z=6..25
on x=-18..33,y=-43..6,z=-21..32
on x=20945..37490,y=-48632..-28197,z=-67171..-48230
on x=71419..77600,y=-22744..-293,z=14363..31802
on x=-44702..-17279,y=70158..87360,z=-19036..6105
on x=-38065..-22040,y=-68705..-60406,z=-35285..-18021
on x=63765..71800,y=-34973..-11393,z=-45557..-23568
on x=-61625..-44728,y=-39652..-23869,z=-70393..-46625
on x=-13631..14261,y=-82169..-69943,z=3373..23688
on x=69565..77803,y=11109..15148,z=-33831..-22858
on x=-56284..-22593,y=36691..50732,z=47039..50098
on x=64552..67483,y=-53093..-20045,z=-36145..-18641
on x=-6053..8047,y=-24144..-11458,z=-77211..-63872
on x=61197..86070,y=6678..24351,z=-15664..-5230
on x=-85663..-62651,y=30647..62481,z=-13236..5486
on x=-35926..-9741,y=-51135..-22619,z=-81780..-53722
on x=-2337..9629,y=-27527..-11196,z=-92190..-64941
on x=-35593..-27483,y=-57957..-38585,z=-73560..-36213
on x=30841..60293,y=-64308..-50318,z=-25973..-599
on x=-62326..-39613,y=-71950..-51587,z=-6193..378
on x=-30695..-9325,y=73673..94063,z=-17322..16949
on x=-11531..9440,y=-18105..-3282,z=73722..80709
on x=28510..57645,y=42624..55911,z=-57016..-43172
on x=54794..75205,y=-34726..-13146,z=-41271..-33051
on x=-66354..-46626,y=37076..53728,z=-26690..-16251
on x=53693..74279,y=-64257..-25693,z=-31013..-13928
on x=24718..29596,y=-50435..-26038,z=-68806..-49962
on x=-78803..-58702,y=3305..14444,z=22053..47523
on x=33005..35082,y=-21045..784,z=68523..82829
on x=-44759..-23527,y=-51455..-13134,z=-74307..-55312
on x=38982..55976,y=-68398..-53906,z=20077..43232
on x=919..18546,y=3504..28547,z=-92588..-63079
on x=60549..78461,y=8982..32805,z=-32605..-21465
on x=42924..53244,y=-75269..-46796,z=32247..37760
on x=-68179..-46114,y=-57985..-38992,z=-3374..11906
on x=59390..87062,y=-29059..-20038,z=14024..34894
on x=41936..67582,y=-71765..-48795,z=-17707..2033
on x=20827..56079,y=-44802..-9771,z=58446..77259
on x=57195..93919,y=1305..25152,z=-34566..-13134
on x=-32671..-22898,y=60748..90666,z=-23383..-13785
on x=669..23079,y=28410..37500,z=-91849..-67213
on x=29826..53150,y=49231..60460,z=35595..53892
on x=-80516..-51010,y=-11948..509,z=-57533..-25261
on x=-16810..18046,y=-40054..-30140,z=53826..78337
on x=-30727..-19331,y=-45538..-12018,z=-72971..-61087
on x=-7712..3601,y=-88880..-66644,z=296..6668
on x=-10553..7841,y=-15605..2447,z=78368..81863
on x=-48263..-12697,y=-21168..-2718,z=-86088..-53962
on x=-82812..-57188,y=-11012..6919,z=-40512..-31439
on x=500..9097,y=-22526..-148,z=71214..85371
on x=-47668..-31834,y=65268..67654,z=4398..34171
on x=21817..57749,y=-62296..-54913,z=-29644..-25587
on x=17101..46142,y=46983..63341,z=-69525..-39512
on x=-69090..-57835,y=-31476..-10588,z=29401..56099
on x=-36656..-16395,y=-64682..-46179,z=-53007..-37586
on x=-55951..-31436,y=-44140..-39802,z=-65142..-51654
on x=-15876..-2500,y=8346..23346,z=62123..85753
on x=-8152..19669,y=70866..90128,z=-8194..18625
on x=39113..53393,y=48337..69049,z=25675..41617
on x=32979..41363,y=-68129..-40221,z=-61466..-39046
on x=-23474..8535,y=-60652..-34629,z=-77763..-51702
on x=-58044..-20955,y=-62404..-43670,z=-51658..-33370
on x=-23837..-14479,y=-81694..-63358,z=39588..56183
on x=-89899..-60896,y=2727..25947,z=-7898..8482
on x=-60192..-48518,y=2512..20160,z=38210..56648
on x=-61874..-44580,y=-59227..-40958,z=12423..40738
on x=32446..50036,y=45277..81152,z=16464..40088
on x=-28476..-7871,y=-31489..-14605,z=54469..83391
on x=-8519..10107,y=12031..30871,z=67241..94918
on x=13992..40741,y=48664..71638,z=34196..43948
on x=-9625..616,y=38295..57222,z=51422..61851
on x=-47378..-18864,y=-37825..-24137,z=58608..74690
on x=-17400..6605,y=-88568..-68092,z=-27837..-15280
on x=-36598..-19073,y=13856..17074,z=68112..89257
on x=53586..75887,y=-31856..-7711,z=-72488..-35400
on x=12644..43783,y=33486..46661,z=-72658..-54042
on x=-84750..-60591,y=-27279..3934,z=33354..46066
on x=-13073..5947,y=-6367..17014,z=63080..90961
on x=-5916..18969,y=34694..51328,z=-73827..-48008
on x=-52192..-49688,y=-13401..-6625,z=-73898..-58890
on x=-50286..-31887,y=-8344..1098,z=-73980..-70681
on x=5122..19886,y=55681..78482,z=-53895..-32919
on x=68127..91386,y=-1319..9774,z=-40455..-32150
on x=-28263..-11978,y=-73121..-57904,z=5012..27462
on x=-29729..-6448,y=-90955..-61407,z=14195..26776
on x=36479..66376,y=-57151..-32091,z=-50230..-14652
on x=58275..80498,y=945..20341,z=32790..61742
on x=-83596..-70361,y=-44348..-17463,z=-21359..-13364
on x=-7625..16311,y=-68039..-42310,z=49643..74443
on x=-16664..14635,y=-87996..-48726,z=32516..61912
on x=-54103..-36015,y=42571..66086,z=-32417..-9920
on x=45846..73902,y=47060..63329,z=14862..22746
on x=-76654..-55035,y=-45955..-37009,z=-12806..-6160
on x=-55899..-37408,y=-65952..-34477,z=42673..61717
on x=56157..73403,y=21843..35425,z=36881..61054
on x=-7016..12925,y=-28619..-6059,z=61314..78557
on x=21210..39516,y=-1536..19103,z=66029..83566
on x=-6956..19730,y=17376..43257,z=55474..72093
on x=-79364..-57754,y=-46974..-35477,z=-20307..-4398
on x=-90447..-67179,y=-39444..-9726,z=20348..28705
on x=-15757..2167,y=50103..72339,z=46264..71838
on x=-55169..-33065,y=-22293..-4797,z=53628..78825
on x=35838..56589,y=56652..77029,z=-32487..-6995
on x=-55055..-33694,y=53974..80088,z=-16827..982
on x=-20155..4156,y=61115..80080,z=-7961..18252
on x=59223..67168,y=-44233..-29649,z=-38421..-4877
on x=62491..73105,y=-2588..16654,z=31149..52104
on x=11953..32952,y=66517..91344,z=11294..35196
on x=-35759..-19746,y=-34986..-15035,z=-90188..-64360
on x=39790..62424,y=-63043..-31208,z=22315..32373
on x=-78996..-60071,y=-7907..14935,z=-61257..-43116
on x=22339..51450,y=-57682..-32968,z=-63436..-31191
on x=-58384..-41059,y=-65969..-32372,z=-46728..-17259
on x=39125..49523,y=-54841..-40483,z=-51424..-36081
on x=29520..47398,y=50640..78178,z=-26059..-22668
on x=-65784..-34594,y=-67423..-35780,z=-49702..-18486
on x=65844..92590,y=-13903..4767,z=-36550..-23630
on x=-52512..-30571,y=-30341..-19452,z=56395..71874
on x=-21705..898,y=-38787..-16485,z=66186..75879
on x=2551..13765,y=65809..86308,z=29716..46009
on x=-65614..-43816,y=53348..63320,z=-5052..13309
on x=47423..56271,y=38493..77500,z=-27339..-14400
on x=-80182..-43047,y=-42189..-37009,z=-39527..-31560
on x=43995..57082,y=-66079..-49033,z=-46335..-22763
on x=-59627..-39370,y=57377..58076,z=18158..33569
on x=50727..58491,y=2271..22746,z=-74014..-36593
on x=48896..75845,y=16223..41313,z=38887..50400
on x=-28504..-23732,y=-90784..-69719,z=15933..29161
on x=-31960..6664,y=48933..68230,z=-72977..-47891
on x=53900..71731,y=-63619..-38122,z=10413..46806
on x=-19672..-2077,y=-8975..1170,z=-97168..-61772
on x=-13564..9192,y=63101..92554,z=7476..8464
on x=-89004..-61564,y=-23174..-467,z=-17140..-7994
on x=44235..73315,y=-39962..-10080,z=-66656..-43076
on x=-78419..-57509,y=33181..52532,z=-34362..-4187
on x=37623..57322,y=-34382..-14393,z=56816..76461
on x=-89780..-67550,y=-37935..-14039,z=4332..26678
on x=30843..53536,y=55060..76003,z=-23945..3714
on x=16350..24067,y=-79332..-57223,z=-33154..-4358
on x=-12786..-466,y=18285..33454,z=58566..88618
on x=-66549..-53970,y=-66965..-51066,z=-7489..23082
on x=-89498..-66783,y=-5083..29108,z=-2113..18162
on x=-43098..-36488,y=-22214..-729,z=-83402..-54386
on x=-41257..-29176,y=-57118..-30011,z=40714..61116
on x=27799..54650,y=-3304..7420,z=62450..74231
on x=-46216..-26081,y=2923..24282,z=-65987..-63494
on x=25846..36272,y=45486..69201,z=-58624..-29343
on x=-19478..-2205,y=-13238..9555,z=60139..95207
on x=33097..67631,y=-27663..-13594,z=50650..72011
on x=34243..42360,y=-60148..-32809,z=-63961..-33194
on x=-4603..24090,y=-69231..-36190,z=-59837..-40784
on x=-35305..-8580,y=45315..54552,z=56193..69806
on x=-79318..-54892,y=36081..51506,z=-32224..-9386
on x=50083..78984,y=-2995..16341,z=-53861..-30648
on x=16704..24183,y=65726..77929,z=2245..25331
on x=-62571..-47153,y=26516..46080,z=33301..50391
on x=70105..89967,y=-9665..3801,z=27723..49057
on x=34485..47945,y=-46196..-21261,z=-64711..-41867
on x=49328..63942,y=-63737..-56173,z=-5358..15183
on x=27743..39056,y=56715..76686,z=20937..46628
on x=-4387..26574,y=40266..57305,z=-77248..-55160
on x=45829..76645,y=-27175..-5284,z=33533..55720
on x=-97755..-64159,y=-14420..6557,z=-28324..-10764
on x=-27220..-12541,y=71801..87972,z=2822..7108
on x=-30818..-7301,y=-85064..-63328,z=33576..46665
on x=75730..87444,y=-26285..-8861,z=-15315..-9266
on x=29194..52084,y=-73664..-47268,z=14463..48945
on x=-44918..-26274,y=1877..22861,z=64475..81410
on x=-48104..-24172,y=-21216..550,z=66602..78210
on x=55609..76877,y=14743..33294,z=34105..47504
on x=-12991..8722,y=15002..46728,z=-88138..-60537
on x=-3114..28333,y=-24076..-1673,z=68220..80472
on x=44120..65987,y=-31184..-13876,z=45649..61061
on x=-16798..9054,y=66835..79639,z=19357..38719
on x=15629..39321,y=-39422..-12707,z=52014..76099
on x=48953..69754,y=-53521..-21796,z=-32378..3497
on x=33343..62189,y=-3978..13390,z=50595..68494
on x=-8188..21673,y=70681..89779,z=14937..30388
on x=-36092..-19673,y=-1925..13966,z=-85430..-53197
on x=44446..63622,y=-56639..-43893,z=-50522..-36248
on x=-47252..-13169,y=8858..27077,z=71313..78824
on x=53539..78661,y=37667..47354,z=-40046..-10726
on x=11053..38743,y=53791..90827,z=14292..19970
on x=-81640..-69761,y=-20263..-9828,z=-24946..416
on x=-87209..-70463,y=-43310..-26177,z=-11965..-806
on x=32006..63592,y=-66623..-44967,z=19207..43525
on x=24227..63344,y=-8519..-2522,z=65048..85801
on x=-5845..20004,y=-76424..-55077,z=28465..49518
on x=-23540..-8873,y=-29053..-7445,z=-93395..-70964
on x=-22873..-12522,y=-79867..-59750,z=-12290..4913
on x=-86708..-60862,y=-3162..1594,z=34786..45058
on x=-58933..-37769,y=-16069..7667,z=44506..61220
on x=9474..20144,y=-82825..-48642,z=-59635..-26126
on x=36698..43971,y=5234..15864,z=68606..84551
on x=25637..48478,y=41031..58748,z=41380..70287
on x=11427..34486,y=13300..41305,z=64417..75585
on x=58826..93372,y=-31259..1484,z=-31983..-20400
on x=30288..40004,y=44189..66924,z=-65663..-29294
on x=-41356..-22594,y=-64816..-53063,z=-60511..-45249
on x=-70025..-53171,y=-63995..-35849,z=-10094..5304
on x=23085..46040,y=-14087..11839,z=-83278..-64519
on x=44488..78366,y=2871..27173,z=40798..59310
off x=22311..57943,y=43488..65178,z=8153..33986
on x=-6271..5040,y=61736..86668,z=13978..45050
off x=-52915..-28509,y=-38725..-20965,z=-70016..-50612
on x=-46447..-13657,y=-75840..-60420,z=-39305..-23734
on x=-34604..-9211,y=-87561..-59830,z=-36836..1804
off x=-4183..4655,y=-76176..-55154,z=29111..44167
off x=33285..55605,y=25133..31740,z=57276..70736
off x=71114..94973,y=-35882..1116,z=8253..32162
on x=-80327..-69876,y=-4869..18971,z=-45342..-21183
off x=-54129..-39502,y=57965..71903,z=22203..32546
on x=18196..36520,y=-78447..-76717,z=-8606..6694
off x=-37047..-23409,y=53953..86313,z=547..25294
off x=-68040..-60315,y=-2471..15246,z=-58494..-27735
on x=30882..36576,y=56038..72358,z=-21292..-13583
on x=-47620..-27436,y=-74744..-56777,z=-48083..-25985
off x=-78883..-52512,y=18090..25867,z=40060..50680
off x=-69941..-48841,y=28345..49720,z=-36655..-10805
on x=-18071..1079,y=26730..42065,z=-83531..-67215
on x=-91736..-61676,y=-25929..9997,z=-39246..-17984
off x=-75709..-61641,y=37773..54326,z=9651..33521
on x=62420..71291,y=17103..43621,z=-30317..-21026
off x=25011..46652,y=-71650..-46774,z=-50993..-36914
on x=1956..18056,y=57821..78551,z=-62775..-41039
on x=-42194..-21260,y=15208..42287,z=61567..77167
on x=-6757..32220,y=-60769..-46234,z=-76673..-54661
off x=39638..67968,y=-16069..13497,z=-75922..-42340
on x=56601..70496,y=13848..25084,z=25543..63467
off x=-72117..-42874,y=-8800..15624,z=-56927..-52328
on x=47502..76386,y=34216..51247,z=25562..52899
on x=-39378..-32624,y=-88822..-70333,z=-25325..5988
off x=67356..88751,y=-32061..-9439,z=5079..24585
on x=-56025..-38109,y=-66559..-58999,z=-28628..-13479
on x=-83249..-63831,y=-20387..1077,z=3427..15304
off x=32717..47901,y=-54725..-45008,z=35436..69806
on x=1299..13705,y=4224..39004,z=-80220..-58987
off x=-12611..5156,y=-14943..2568,z=77809..95365
on x=57103..68606,y=-26105..-1552,z=35353..68216
off x=-68514..-38393,y=-40940..-4440,z=-56017..-45694
off x=-66729..-40243,y=19879..39968,z=53424..73280
on x=-29093..-15955,y=466..27508,z=69016..86086
off x=-329..15423,y=-16247..9972,z=60411..87178
off x=-42169..-19535,y=-10437..14868,z=67780..79157
on x=-24264..-1506,y=71457..75784,z=17572..35372
on x=11909..30299,y=-74323..-36205,z=48824..54161
on x=45499..64279,y=-51011..-20153,z=44094..62174
on x=-31168..-19020,y=46092..56952,z=-56117..-38016
on x=52649..77647,y=-51680..-39781,z=-26441..273
on x=10643..14764,y=49102..59161,z=46809..75418
on x=-57575..-37565,y=43843..77635,z=-22521..-1263
off x=27101..59535,y=-36192..-12169,z=-76573..-40556
off x=-51130..-25659,y=-64446..-38991,z=-57846..-31064
off x=-5717..10808,y=-15433..-4773,z=72657..82815
on x=-12643..11516,y=-10534..20440,z=-85179..-73229
off x=36072..52145,y=62432..76687,z=-10807..3053
on x=33211..51338,y=-78519..-55147,z=-15170..8362
off x=28578..42288,y=45754..75324,z=-42689..-25875
off x=-17463..20742,y=-75385..-63289,z=-48090..-19748
off x=-5435..19681,y=-81350..-77354,z=-28..1724
off x=-91701..-58137,y=-30844..-24584,z=-25923..-3145
off x=-61484..-27973,y=45052..68042,z=-43190..-10531
off x=-3625..6220,y=20512..38321,z=66183..85027
on x=-70801..-52672,y=-55480..-34303,z=7088..26838
on x=41388..46465,y=-34293..-7487,z=-64547..-45028
off x=71687..86102,y=26282..45152,z=-22201..-6541
off x=-7472..16062,y=-51641..-32625,z=-71702..-53643
off x=-37998..-27622,y=-82030..-66284,z=-10960..-2128
on x=-2176..6136,y=-84870..-71245,z=-47613..-20545
off x=2023..15209,y=25429..57508,z=65332..79325
on x=66306..78678,y=14586..19973,z=15494..31575
off x=-28288..-4093,y=-58965..-36833,z=-75086..-43491
on x=-35664..-5450,y=55093..74197,z=11686..40118
on x=-25067..-3663,y=-79278..-72206,z=15111..42777
on x=-48705..-16750,y=-31817..-10749,z=-84005..-58825
on x=-67394..-48753,y=-30724..-12321,z=30269..50430
off x=-75640..-58478,y=-35252..-26181,z=14942..43731
on x=3645..5324,y=-73185..-65733,z=-40243..-14743
on x=-66735..-60945,y=-38128..-24332,z=-49348..-22212
off x=39266..43856,y=-68976..-43493,z=-62077..-35863
on x=-81074..-53023,y=-42692..-11626,z=24132..50317
on x=39849..70221,y=-7159..27862,z=42258..66445
on x=36944..64796,y=33842..46805,z=44049..66367
off x=71910..87909,y=-19051..10763,z=-11396..-5657
on x=-67126..-37081,y=-51245..-28315,z=21307..54257
off x=-19169..17107,y=22197..53541,z=52907..85793
off x=21921..51605,y=62863..86554,z=3217..29145
off x=-23080..-13303,y=-96866..-61724,z=-10145..8727
on x=74959..90774,y=-21729..4994,z=-30247..-12664
on x=-97043..-69306,y=-24917..10436,z=-31138..-12623
on x=-87289..-73742,y=-10720..13743,z=2049..31088
off x=33116..46142,y=64648..65730,z=-46808..-25557
on x=39542..43019,y=8959..30233,z=-66252..-50295
on x=65866..84296,y=26894..57421,z=13822..36340
on x=19872..48034,y=63173..79660,z=9327..25000
on x=26065..34252,y=14357..32370,z=67493..86165
off x=44637..50180,y=31605..59353,z=-64989..-32725
on x=34275..59056,y=-63617..-52263,z=23254..47216
off x=17347..45490,y=42936..77410,z=-58828..-33819
on x=-77246..-67310,y=-21452..-6496,z=-10608..-7788
on x=59287..68922,y=-33436..493,z=36722..49008
on x=-67621..-50401,y=41712..46222,z=29218..49441
off x=15985..44037,y=-5356..24567,z=64150..77862
on x=58298..93548,y=10095..38425,z=-15661..5002
on x=-62628..-39905,y=-74527..-40419,z=-39220..-17999
off x=-74644..-55520,y=-36577..-33128,z=24670..38098
off x=-79006..-60340,y=-29992..766,z=1589..39530
on x=70968..75959,y=-6337..31310,z=13795..45722
off x=-19679..-4435,y=-78689..-67151,z=27628..55041
off x=56974..94479,y=-1609..15433,z=11323..27181
off x=66858..79428,y=12026..27799,z=5678..36098
off x=-15997..4614,y=-77442..-55493,z=-45736..-40211
on x=-79325..-53991,y=-51227..-16674,z=1572..20540
off x=-84204..-78115,y=-14186..23161,z=7984..19520
off x=14180..52001,y=-43218..-4276,z=51733..73003
on x=-47745..-27031,y=62212..80274,z=26289..35385
on x=36495..56909,y=-68887..-47004,z=2831..24821
on x=-67954..-59845,y=-51052..-44042,z=4753..24626
on x=-52827..-36489,y=-79553..-56624,z=-4119..19101
on x=76183..80637,y=-12874..6745,z=-6558..20532
on x=-72110..-69780,y=21906..44158,z=7585..28179
off x=-9993..22199,y=-58098..-41582,z=52706..68723
off x=-71885..-55936,y=-42675..-31541,z=27950..34300
on x=40606..68339,y=28438..43278,z=34434..52103
on x=-25545..2678,y=65242..89455,z=-1423..8234
on x=-31639..-4297,y=59195..83177,z=-25674..-2536
off x=15742..37250,y=-78064..-39216,z=30757..62358
off x=-50759..-20539,y=-77148..-52448,z=-53238..-34329
on x=-77513..-53028,y=-38550..-14591,z=16397..37474
on x=-38262..-16201,y=61522..86821,z=20771..54961
off x=-71374..-43847,y=-57124..-44595,z=19802..46595
off x=-65222..-37268,y=38786..40210,z=47758..53007
on x=-3205..27279,y=77392..85402,z=-1195..11237
on x=31281..65902,y=-68276..-56764,z=-13742..7138
on x=-33043..671,y=-54607..-30571,z=-82315..-60065
off x=4943..9094,y=-66607..-41312,z=-64162..-50915
off x=44875..60559,y=18574..37638,z=58856..73441
off x=40822..52957,y=-11660..9750,z=-74087..-50297
off x=-49638..-32547,y=-24883..-492,z=59440..70247
on x=-78606..-74696,y=-2991..29605,z=17480..26777
off x=-23030..-4972,y=-18991..-871,z=65805..85361
off x=54633..77831,y=21381..43385,z=-2152..20877
on x=15077..31796,y=67441..76316,z=-45758..-24043
on x=24279..42141,y=-19253..-8795,z=-77472..-58165
off x=-68909..-42358,y=-35797..-31750,z=28531..49334
off x=-46552..-19849,y=8232..17953,z=-80999..-66385
off x=20154..27905,y=66927..80394,z=-12334..-431
off x=-30409..-10861,y=-81984..-51763,z=-37779..-23157
off x=48142..58010,y=40917..69835,z=1861..25434
off x=51475..74469,y=-72288..-48781,z=-15178..6905
off x=53695..85451,y=14843..32605,z=-34859..-8099
on x=-27999..-4241,y=-70587..-54297,z=-42964..-36502
off x=34223..38040,y=-60058..-45192,z=41343..62121
on x=-27118..-17100,y=9876..30369,z=59974..89083
on x=57414..84116,y=653..24930,z=12781..16051
on x=-68059..-46789,y=47743..61716,z=12060..24489
off x=44412..49101,y=-45623..-20751,z=39617..59410
on x=-86423..-55173,y=20275..38214,z=9484..36139
on x=-38830..-22580,y=40100..62317,z=42077..61533
off x=24696..50805,y=-89530..-55640,z=520..4677
on x=-46166..-20776,y=-79989..-72077,z=4582..25927
on x=43811..65829,y=50958..63439,z=-2090..22107
on x=-66076..-33140,y=26478..43156,z=48192..64457
off x=18804..39958,y=-15990..-593,z=71288..94017
on x=-16645..15074,y=-2645..15795,z=74709..84293
off x=56716..67842,y=-15454..6183,z=36839..53509
on x=-45892..-38147,y=-67059..-39339,z=27919..58645
on x=-14633..8662,y=36566..49728,z=63050..77229
on x=-31392..-20479,y=-69483..-49570,z=54352..65770
on x=26583..37737,y=-36785..-16489,z=-83413..-50685
off x=40914..63631,y=46870..83142,z=-7933..19092
off x=22218..50507,y=-83680..-62121,z=-51260..-23449
off x=9756..44847,y=-54580..-35603,z=-73506..-46562
off x=-17647..11878,y=-98332..-77705,z=-14684..15002
on x=-44741..-26271,y=-81716..-54482,z=-11477..14070
off x=-35076..-1981,y=73759..83940,z=-21950..7270
on x=-19226..1824,y=51503..59762,z=-65845..-48821
off x=6570..26998,y=-11458..-3080,z=-78953..-67997
on x=70248..84575,y=-24679..-19753,z=-5858..14661
off x=-28439..-2720,y=-49367..-31350,z=-69392..-60563
on x=38186..43046,y=-38739..-14721,z=53616..64985
on x=18674..46606,y=-2887..13267,z=-85821..-71134
on x=-82280..-77000,y=-2546..19659,z=5979..28022
on x=9308..25379,y=853..10768,z=-96427..-62739
on x=-12253..15167,y=67595..84457,z=35629..49242
off x=60833..90816,y=-4001..21790,z=6093..32304
on x=-32813..-4065,y=28993..56664,z=49630..78321
on x=-23974..-17100,y=21409..34139,z=60823..84023
on x=-5326..9926,y=36021..52378,z=-73176..-60034
off x=-20642..1352,y=-81150..-62097,z=-42609..-24758
on x=-794..19841,y=-87169..-60122,z=-36505..-19502
off x=34975..63805,y=25802..46849,z=-58820..-38132
on x=39619..46293,y=342..22958,z=56234..80879
on x=48123..48729,y=14420..32784,z=-66490..-58380
on x=-52615..-37665,y=-75901..-52492,z=9849..26733
on x=41721..61120,y=27102..48745,z=-64930..-40204
off x=-78289..-60041,y=14354..29875,z=-54529..-41303
off x=44318..74454,y=-14021..-2636,z=-63197..-30743
on x=27378..53613,y=39087..49720,z=33420..69852
off x=-19620..2036,y=7441..31215,z=69825..86519
on x=15791..19130,y=-76450..-47614,z=-58381..-42389
on x=-35094..-12809,y=66272..89209,z=22037..50934"""
