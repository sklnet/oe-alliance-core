macprog2 AC-DB-EF-11-22-33

flash -noheader -forceerase usbdisk0:gigablue/seplus/kernel.bin nandflash0.kernel

flash -noheader -forceerase usbdisk0:gigablue/seplus/rootfs.bin nandflash0.rootfs

setenv -p STARTUP "boot -z -elf nandflash0.kernel:"

boot -z -elf nandflash0.kernel:
