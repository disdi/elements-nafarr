// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package nafarr.system.dma

import spinal.core._

/** Level-sensitive DMA request lines of a FIFO-backed IP.
  *
  * tx: the write-side FIFO can accept one element (DMA memory -> peripheral).
  * rx: the read-side FIFO holds at least one element (DMA peripheral -> memory).
  */
case class DmaRequest() extends Bundle {
  val tx = Bool()
  val rx = Bool()
}
