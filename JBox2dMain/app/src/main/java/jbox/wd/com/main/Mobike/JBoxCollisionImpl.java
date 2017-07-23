package jbox.wd.com.main.Mobike;

import android.view.View;
import android.view.ViewGroup;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.Random;

import jbox.wd.com.main.R;

public class JBoxCollisionImpl {
    private World mWorld;
    private int mWorldWidth, mWorldHeight;
    private ViewGroup viewGroup;
    private Random random = new Random();

    private float dt = 1f / 60f;
    private int velocityIterations = 5;
    private int positionIterations = 20;

    private int mProportion = 50;
    private float mDensity = 0.6f;
    private float mFrictionRatio = 0.8f;
    private float mRestitutionRatio = 0.6f;


    public JBoxCollisionImpl(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
        mDensity = viewGroup.getContext().getResources().getDisplayMetrics().density;
    }

    private void createWorld() {
        if (mWorld == null) {
            mWorld = new World(new Vec2(0, 10.0f));
            updateTopAndBottomBounds();
            updateLeftAndRightBounds();
        }
    }

    private void createWorldChild(boolean change) {
        if (viewGroup != null) {
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = viewGroup.getChildAt(i);
                if (!isBodyView(view) || change) {
                    createBody(view);
                }
            }
        }

    }

    private void createBody(View view) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;

        bodyDef.position.set(mappingView2Body(view.getX() + view.getWidth() / 2), mappingView2Body(view.getY() + view.getHeight() / 2));

        Shape shape = null;
        Boolean isCircle = (Boolean) view.getTag(R.id.wd_view_circle_tag);
        if (isCircle != null && isCircle) {
            shape = createCircleBody(view);
        } else {
            shape = createPolygonBody(view);
        }

        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.density = mDensity;
        def.friction = mFrictionRatio;
        def.restitution = mRestitutionRatio;

        Body body = mWorld.createBody(bodyDef);
        body.createFixture(def);
        view.setTag(R.id.wd_view_body_tag, body);

        body.setLinearVelocity(new Vec2(random.nextFloat(), random.nextFloat()));
    }

    public void onSizeChanged(int w, int h) {
        this.mWorldWidth = w;
        this.mWorldHeight = h;
    }

    public void onLayout(boolean changed) {
        createWorld();
        createWorldChild(changed);
    }

    public void onDraw() {
        if (mWorld != null) {
            mWorld.step(dt, velocityIterations, positionIterations);
        }
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (isBodyView(view)) {
                view.setX(getViewX(view));
                view.setY(getViewY(view));
                view.setRotation(getViewRotaion(view));
            }
        }
        viewGroup.invalidate();
    }


    private void updateTopAndBottomBounds() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;

        PolygonShape shape = new PolygonShape();
        float hx = mappingView2Body(mWorldWidth);
        float hy = mappingView2Body(mProportion);
        shape.setAsBox(hx, hy);

        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.density = mDensity;
        def.friction = mFrictionRatio;
        def.restitution = mRestitutionRatio;

        bodyDef.position.set(0, -hy);
        Body topBody = mWorld.createBody(bodyDef);
        topBody.createFixture(def);

        bodyDef.position.set(0, mappingView2Body(mWorldHeight) + hy);
        Body bottomBody = mWorld.createBody(bodyDef);
        bottomBody.createFixture(def);
    }

    private void updateLeftAndRightBounds() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;

        PolygonShape shape = new PolygonShape();
        float hx = mappingView2Body(mProportion);
        float hy = mappingView2Body(mWorldHeight);
        shape.setAsBox(hx, hy);

        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.density = mDensity;
        def.friction = mFrictionRatio;
        def.restitution = mRestitutionRatio;

        bodyDef.position.set(-hx, hy);
        Body leftBody = mWorld.createBody(bodyDef);
        leftBody.createFixture(def);

        bodyDef.position.set(mappingView2Body(mWorldWidth) + hx, 0);
        Body rightBody = mWorld.createBody(bodyDef);
        rightBody.createFixture(def);
    }


    private Shape createCircleBody(View view) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(mappingView2Body(view.getWidth() / 2));
        return circleShape;
    }

    private Shape createPolygonBody(View view) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(mappingView2Body(view.getWidth() / 2), mappingView2Body(view.getHeight() / 2));
        return shape;
    }

    private float mappingView2Body(float view) {
        return view / mProportion;
    }

    private float mappingBody2View(float body) {
        return body * mProportion;
    }


    private void applyLinearImpulse(float x, float y, View view) {
        Body body = (Body) view.getTag(R.id.wd_view_body_tag);
        Vec2 vec2 = new Vec2(x, y);
        body.applyLinearImpulse(vec2, body.getPosition(), true);
    }

    private boolean isBodyView(View view) {
        Body body = (Body) view.getTag(R.id.wd_view_body_tag);
        return body != null;
    }

    private float getViewX(View view) {
        Body body = (Body) view.getTag(R.id.wd_view_body_tag);
        if (body != null) {
            return mappingBody2View(body.getPosition().x) - (view.getWidth() / 2);
        }
        return 0;
    }

    private float getViewY(View view) {
        Body body = (Body) view.getTag(R.id.wd_view_body_tag);
        if (body != null) {
            return mappingBody2View(body.getPosition().y) - (view.getHeight() / 2);
        }
        return 0;
    }

    public float getViewRotaion(View view) {
        Body body = (Body) view.getTag(R.id.wd_view_body_tag);
        if (body != null) {
            float angle = body.getAngle();
            return (angle / 3.14f * 180f) % 360;
        }
        return 0;
    }

    public void onSensorChanged(float x, float y) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (isBodyView(view)) {
                applyLinearImpulse(x, y, view);
            }
        }
    }

    public void onRandomChanged(){
        int childCount = viewGroup.getChildCount();
        float x = random.nextInt(800) - 800;
        float y = random.nextInt(800) - 800;
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (isBodyView(view)) {
                applyLinearImpulse(x, y, view);
            }
        }
    }
}
