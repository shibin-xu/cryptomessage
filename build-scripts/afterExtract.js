module.exports = function(extractPath, electronVersion, platform, arch, done) {
    src = "./assets"
    dst = extractPath+"/assets"
    console.log("extracted")
    console.log(src)
    console.log(dst)
    const fse = require('fs-extra')
    fse.copySync("./assets", extractPath+"/assets")
    fse.copySync("./commons", extractPath+"/commons")
    fse.copySync("./crypto", extractPath+"/crypto")
    fse.copySync("./ipc", extractPath+"/ipc")
    fse.copySync("./certs", extractPath+"/certs")
    done();
};