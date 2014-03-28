require e2fsprogs.inc

PR = "r6"

SRC_URI += "file://acinclude.m4 \
            file://remove.ldconfig.call.patch \
            file://populate-extfs.sh \
"

SRC_URI[md5sum] = "9e444c240c1001b3292d108fbad0f49c"
SRC_URI[sha256sum] = "726b5919edeaee5b077d7a5b602284f1453ea31cf499efa433af26c679b667a5"

EXTRA_OECONF += "--libdir=${base_libdir} --sbindir=${base_sbindir} --enable-elf-shlibs --disable-libuuid --disable-uuidd"
EXTRA_OECONF_darwin = "--libdir=${base_libdir} --sbindir=${base_sbindir} --enable-bsd-shlibs"
EXTRA_OECONF_darwin8 = "--libdir=${base_libdir} --sbindir=${base_sbindir} --enable-bsd-shlibs"

do_configure_prepend () {
    cp ${WORKDIR}/acinclude.m4 ${S}/
}

do_compile_prepend () {
    find ./ -print | grep -v ./patches | xargs chmod u=rwX
    ( cd util; ${BUILD_CC} subst.c -o subst )
}

do_install () {
    oe_runmake 'DESTDIR=${D}' install
    oe_runmake 'DESTDIR=${D}' install-libs
    # We use blkid from util-linux now so remove from here
    rm -f ${D}${base_libdir}/libblkid*
    rm -rf ${D}${includedir}/blkid
    rm -f ${D}${base_libdir}/pkgconfig/blkid.pc
    install -m 0755 ${WORKDIR}/populate-extfs.sh ${D}${bindir}
}

do_install_append () {
    # e2initrd_helper and the pkgconfig files belong in libdir
    if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
        install -d ${D}${libdir}
        mv ${D}${base_libdir}/e2initrd_helper ${D}${libdir}
        mv ${D}${base_libdir}/pkgconfig ${D}${libdir}
    fi
}

# blkid used to be part of e2fsprogs but is useful outside, add it
# as an RDEPENDS_${PN} so that anything relying on it being in e2fsprogs
# still works
RDEPENDS_e2fsprogs = "e2fsprogs-blkid e2fsprogs-badblocks"

PACKAGES =+ "e2fsprogs-blkid e2fsprogs-e2fsck e2fsprogs-mke2fs e2fsprogs-fsck e2fsprogs-tune2fs e2fsprogs-badblocks"
PACKAGES =+ "libcomerr libss libe2p libext2fs"

FILES_e2fsprogs-blkid = "${base_sbindir}/blkid"
FILES_e2fsprogs-fsck = "${base_sbindir}/fsck"
FILES_e2fsprogs-e2fsck = "${base_sbindir}/e2fsck ${base_sbindir}/fsck.ext*"
FILES_e2fsprogs-mke2fs = "${base_sbindir}/mke2fs ${base_sbindir}/mkfs.ext* ${sysconfdir}/mke2fs.conf"
FILES_e2fsprogs-tune2fs = "${base_sbindir}/tune2fs ${base_sbindir}/e2label ${base_sbindir}/findfs"
FILES_e2fsprogs-badblocks = "${base_sbindir}/badblocks"
FILES_libcomerr = "${base_libdir}/libcom_err.so.*"
FILES_libss = "${base_libdir}/libss.so.*"
FILES_libe2p = "${base_libdir}/libe2p.so.*"
FILES_libext2fs = "${libdir}/e2initrd_helper ${base_libdir}/libext2fs.so.*"
FILES_${PN}-dev += "${datadir}/*/*.awk ${datadir}/*/*.sed ${base_libdir}/*.so"

BBCLASSEXTEND = "native"
