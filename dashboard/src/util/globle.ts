class Util {
  static token: string = "";
}
function replaceUrlWithNewUrl(url: string): string {
  return url.replace("http://0.0.0.0", "http://localhost");
}

export { Util,replaceUrlWithNewUrl };
