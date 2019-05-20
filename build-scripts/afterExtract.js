module.exports = function(extractPath, electronVersion, platform, arch, done) {
    src = ".\\assets"
    dst = extractPath+"\\assets"
    console.log("extracted")
    console.log(src)
    console.log(dst)
    const fse = require('fs-extra')
    fse.copySync(src, dst)
    done();
};