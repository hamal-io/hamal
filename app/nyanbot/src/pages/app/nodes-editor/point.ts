class Point {
    x: number;
    y: number;

    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    plus(point) {
        return new Point(this.x + point.x, this.y + point.y);
    }

    minus(point) {
        return new Point(this.x - point.x, this.y - point.y);
    }

    length(point) {
        const dx = this.x - point.x;
        const dy = this.y - point.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

export default Point;