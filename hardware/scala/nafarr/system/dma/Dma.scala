// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package nafarr.system.dma

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.tilelink.{
  Bus => TileLinkBus,
  BusParameter => TileLinkParameter,
  SlaveFactory => TileLinkSlaveFactory
}
import spinal.lib.bus.wishbone._

object Dma {

  class Core[T <: spinal.core.Data with IMasterSlave](
      p: DmaCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val mem = master(TileLinkBus(p.memParam))
      val request = (p.requestLines > 0) generate in(Bits(p.requestLines bits))
      val interrupt = out Bool ()
    }
    val ctrl = DmaCtrl(p)
    val mapper = DmaCtrl.Mapper(factory(io.bus), ctrl, p)
    io.mem <> ctrl.io.mem
    if (p.requestLines > 0) {
      ctrl.io.request := io.request
    }
    io.interrupt := mapper.interrupt
  }
}

case class Apb3Dma(
    p: DmaCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends Dma.Core[Apb3](p, Apb3(busConfig), Apb3SlaveFactory(_))

case class TileLinkDma(
    p: DmaCtrl.Parameter,
    busConfig: TileLinkParameter = TileLinkParameter.simple(12, 32, 4, 1)
) extends Dma.Core[TileLinkBus](p, TileLinkBus(busConfig), new TileLinkSlaveFactory(_, false))

case class WishboneDma(
    p: DmaCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends Dma.Core[Wishbone](p, Wishbone(busConfig), WishboneSlaveFactory(_))
