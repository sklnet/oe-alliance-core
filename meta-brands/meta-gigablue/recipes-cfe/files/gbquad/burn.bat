macprog2 AC-DB-EF-11-22-33

flash -noheader -forceerase usbdisk0:gigablue/quad/kernel.bin flash0.kernel

flash -noheader -forceerase usbdisk0:gigablue/quad/rootfs.bin flash0.rootfs

setenv -p STARTUP "boot -z -elf flash0.kernel:"

boot -z -elf flash0.kernel:
